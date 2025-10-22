package com.cibertec.pos_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cibertec.pos_system.entity.ArqueoCajaEntity;
import com.cibertec.pos_system.entity.CajaEntity;
import com.cibertec.pos_system.entity.CajaSesionEntity;
import com.cibertec.pos_system.entity.UsuarioEntity;
import com.cibertec.pos_system.repository.UsuarioRepository;
import com.cibertec.pos_system.repository.CajaVentaRepository;
import com.cibertec.pos_system.service.ArqueoCajaService;
import com.cibertec.pos_system.service.CajaService;
import com.cibertec.pos_system.service.CajaSesionService;
import com.cibertec.pos_system.service.CajaVentaService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/caja-sesion")
public class CajaSesionController {

    @Autowired
    private CajaSesionService sesionService;

    @Autowired
    private CajaService cajaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CajaVentaRepository cajaVentaRepository;

    @Autowired
    private ArqueoCajaService arqueoCajaService;

    @Autowired
    private CajaVentaService cajaVentaService;

    @Autowired
    private CajaSesionService cajaSesionService;

        @GetMapping
    public String listarSesiones(@RequestParam(value = "q", required = false) String q, Model model) {
        // Obtener usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActual = authentication.getName();
        UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioActual);

        // Verificar si es ADMIN
        boolean esAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        // TODOS ven todas las sesiones (cambio aquí)
        List<CajaSesionEntity> sesiones;
        if (q != null && !q.isEmpty()) {
            sesiones = sesionService.buscarPorCualquierCampo(q);
        } else {
            sesiones = sesionService.listar();
        }
        
        // Obtener ID de la caja abierta por el usuario actual (para cajeros)
        Long cajaSesionUsuarioActual = null;
        if (!esAdmin) {
            cajaSesionUsuarioActual = sesiones.stream()
                .filter(s -> s.getUsuarioApertura() != null && 
                            s.getUsuarioApertura().getId().equals(usuarioSesion.getId()) &&
                            "ABIERTA".equals(s.getEstado()))
                .map(CajaSesionEntity::getId)
                .findFirst()
                .orElse(null);
        }
        
        List<CajaEntity> cajas = cajaService.listar();

        Map<Long, CajaSesionEntity> sesionesActivas = new HashMap<>();
        for (CajaSesionEntity sesion : sesiones) {
            if ("ABIERTA".equals(sesion.getEstado())) {
                sesionesActivas.put(sesion.getCaja().getId(), sesion);
            }
        }

        model.addAttribute("sesiones", sesiones);
        model.addAttribute("cajas", cajas);
        model.addAttribute("sesionesActivas", sesionesActivas);
        model.addAttribute("fechaActual", LocalDateTime.now());
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("cajaSesionUsuarioActual", cajaSesionUsuarioActual); // NUEVO

        return "caja/caja-sesion";
    }


@PostMapping("/abrir")
public String abrirCaja(
        @RequestParam Long cajaId,
        @RequestParam double montoInicial,
        RedirectAttributes redirectAttributes) {

    // Obtener usuario autenticado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String usuarioActual = authentication.getName();
    UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioActual);

    // Verificar si es ADMIN
    boolean esAdmin = authentication.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

    // Validar si el usuario ya tiene una caja abierta (SOLO para NO-ADMIN)
    if (!esAdmin) {
        boolean tieneCajaAbierta = sesionService.listar().stream()
            .anyMatch(s -> s.getUsuarioApertura() != null
                && s.getUsuarioApertura().getId().equals(usuarioSesion.getId())
                && "ABIERTA".equals(s.getEstado()));

        if (tieneCajaAbierta) {
            redirectAttributes.addFlashAttribute("errorApertura", "Ya tienes una caja abierta. Debes cerrarla antes de abrir otra.");
            return "redirect:/caja";
        }
    }

    // Verificar si la caja específica ya está abierta (para TODOS)
    boolean cajaYaAbierta = sesionService.listar().stream()
        .anyMatch(s -> s.getCaja().getId().equals(cajaId) && "ABIERTA".equals(s.getEstado()));

    if (cajaYaAbierta) {
        redirectAttributes.addFlashAttribute("errorApertura", "Esta caja ya tiene una sesión abierta.");
        return "redirect:/caja";
    }

    // Si no tiene caja abierta, abrir nueva sesión
    CajaSesionEntity sesion = new CajaSesionEntity();
    sesion.setCaja(cajaService.obtener(cajaId).orElseThrow());
    sesion.setFechaApertura(LocalDateTime.now());
    sesion.setMontoInicial(montoInicial);
    sesion.setEstado("ABIERTA");
    sesion.setUsuarioApertura(usuarioSesion);

    sesionService.guardar(sesion);
redirectAttributes.addAttribute("aperturaExitosa", true);  // ← CAMBIAR ESTA LÍNEA
return "redirect:/caja";
}

@GetMapping("/detalle/{id}")
public String detalleSesion(@PathVariable Long id, Model model) {
    // Trae la sesión con ventas
    CajaSesionEntity sesion = sesionService.obtenerConVentas(id).orElse(null);
    if (sesion == null) {
        model.addAttribute("error", "Sesión no encontrada");
        return "caja/caja-sesion";
    }

    // Buscar el último arqueo de caja para esta sesión (si existe)
    ArqueoCajaEntity arqueo = arqueoCajaService.obtenerUltimoPorSesion(sesion.getId()).orElse(null);

    // Ventas con tarjeta - MODIFICADO para incluir DNI
    List<Object[]> ventasTarjetaOriginal = cajaVentaRepository.findVentasTarjetaPorSesion(id);
    List<Object[]> ventasTarjeta = ventasTarjetaOriginal.stream().map(venta -> {
        Object[] ventaConDNI = new Object[6];
        ventaConDNI[0] = venta[0]; // ID Venta
        ventaConDNI[1] = venta[1]; // Monto
        ventaConDNI[2] = venta[2]; // Nombre Cliente (MANTENER)
        ventaConDNI[3] = venta[3]; // Estado
        ventaConDNI[4] = venta[4]; // Fecha
        // Obtener DNI del cliente
        Long ventaId = (Long) venta[0];
        String dniCliente = cajaVentaRepository.findById(ventaId)
            .map(v -> v.getCliente() != null ? v.getCliente().getDni() : null)
            .orElse(null);
        ventaConDNI[5] = dniCliente; // DNI
        return ventaConDNI;
    }).collect(Collectors.toList());
    
    BigDecimal totalTarjeta = BigDecimal.ZERO;
    for (Object[] v : ventasTarjetaOriginal) {
        if (v[1] != null) totalTarjeta = totalTarjeta.add((BigDecimal) v[1]);
    }

    // Ventas en efectivo - MODIFICADO para incluir DNI
    List<Object[]> ventasEfectivoOriginal = cajaVentaRepository.findVentasEfectivoPorSesion(id);
    List<Object[]> ventasEfectivo = ventasEfectivoOriginal.stream().map(venta -> {
        Object[] ventaConDNI = new Object[6];
        ventaConDNI[0] = venta[0]; // ID Venta
        ventaConDNI[1] = venta[1]; // Monto
        ventaConDNI[2] = venta[2]; // Nombre Cliente (MANTENER)
        ventaConDNI[3] = venta[3]; // Estado
        ventaConDNI[4] = venta[4]; // Fecha
        // Obtener DNI del cliente
        Long ventaId = (Long) venta[0];
        String dniCliente = cajaVentaRepository.findById(ventaId)
            .map(v -> v.getCliente() != null ? v.getCliente().getDni() : null)
            .orElse(null);
        ventaConDNI[5] = dniCliente; // DNI
        return ventaConDNI;
    }).collect(Collectors.toList());
    
    BigDecimal totalEfectivo = BigDecimal.ZERO;
    for (Object[] v : ventasEfectivoOriginal) {
        if (v[1] != null) totalEfectivo = totalEfectivo.add((BigDecimal) v[1]);
    }

    BigDecimal totalGeneral = totalTarjeta.add(totalEfectivo);

    // Pasar los datos al modelo - TODO IGUAL
    model.addAttribute("sesion", sesion);
    model.addAttribute("ventasTarjeta", ventasTarjeta);
    model.addAttribute("totalTarjeta", totalTarjeta);
    model.addAttribute("ventasEfectivo", ventasEfectivo);
    model.addAttribute("totalEfectivo", totalEfectivo);
    model.addAttribute("totalGeneral", totalGeneral);
    model.addAttribute("montoSistema", arqueo != null ? arqueo.getMontoSistema() : null);
    model.addAttribute("diferencia", arqueo != null ? arqueo.getDiferencia() : null);
    model.addAttribute("observaciones", arqueo != null ? arqueo.getObservaciones() : null);

    return "caja/caja-detalle-sesion";
}

    @PostMapping("/cerrar")
    public String cerrarCaja(
            @RequestParam Long cajaId,
            @RequestParam double montoCierre,
            RedirectAttributes redirectAttributes) {

        CajaSesionEntity sesion = sesionService.obtenerSesionActivaPorCajaId(cajaId);
        if (sesion != null) {
            // Sumar ventas en efectivo de la sesión
            BigDecimal totalEfectivo = cajaVentaRepository.totalVentasEfectivoPorSesion(sesion.getId());

            // LOG de depuración antes de las condiciones
            System.out.println("Entrando a cerrarCaja, montoCierre=" + montoCierre +
                ", totalEfectivo=" + totalEfectivo +
                ", montoInicial=" + sesion.getMontoInicial());

            if (BigDecimal.valueOf(montoCierre).compareTo(totalEfectivo) < 0) {
                redirectAttributes.addFlashAttribute("errorCerrar", "Monto faltante: el monto de cierre es menor a la suma de ventas en efectivo (" + totalEfectivo + ")");
                return "redirect:/caja";
            }
            if (montoCierre < sesion.getMontoInicial()) {
                redirectAttributes.addFlashAttribute("errorCerrar", "Monto de cierre menor a monto inicial, no se puede cerrar la caja");
                return "redirect:/caja";
            }
            sesion.setMontoCierre(montoCierre);
            sesion.setFechaCierre(LocalDateTime.now());
            sesion.setEstado("CERRADA");

            // Asignar usuario de cierre
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioActual = authentication.getName();
            UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioActual);
            sesion.setUsuarioCierre(usuarioSesion);

            // LOG de depuración justo antes de guardar
            System.out.println("DEBUG usuarioCierre: " +
                (sesion.getUsuarioCierre() != null ? sesion.getUsuarioCierre().getId() + " - " + sesion.getUsuarioCierre().getUsername() : "null"));

            sesionService.guardar(sesion);
            redirectAttributes.addFlashAttribute("cierreExitosa", true);
        }
        return "redirect:/caja";
    }

    @PostMapping("/delete")
    public String eliminarCaja(@RequestParam Long id) {
        sesionService.eliminar(id);
        return "redirect:/caja-sesion";
    }

    // --- Endpoint para ver el reporte de una sesión ---
    @GetMapping("/reporte/{sesionId}")
    public String verReporteSesion(@org.springframework.web.bind.annotation.PathVariable Long sesionId, Model model) {
        CajaSesionEntity sesion = sesionService.obtener(sesionId).orElse(null);
        if (sesion == null) {
            model.addAttribute("error", "Sesión no encontrada");
            return "caja/caja-sesion";
        }

        // Datos generales
        String local = sesion.getCaja().getLocal() != null ? sesion.getCaja().getLocal().getNombre() : "";
        String codigoCaja = sesion.getCaja().getCodigo();
        String nombreCaja = sesion.getCaja().getNombre();
        String cajero = sesion.getUsuarioApertura() != null ? sesion.getUsuarioApertura().getUsername() : "";

        // Ventas con tarjeta
        List<Object[]> ventasTarjeta = cajaVentaRepository.findVentasTarjetaPorSesion(sesionId);
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        for (Object[] v : ventasTarjeta) {
            if (v[1] != null) totalTarjeta = totalTarjeta.add((BigDecimal) v[1]);
        }

        // Ventas en efectivo
        List<Object[]> ventasEfectivo = cajaVentaRepository.findVentasEfectivoPorSesion(sesionId);
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        for (Object[] v : ventasEfectivo) {
            if (v[1] != null) totalEfectivo = totalEfectivo.add((BigDecimal) v[1]);
        }

        BigDecimal totalGeneral = totalTarjeta.add(totalEfectivo);

        model.addAttribute("local", local);
        model.addAttribute("codigoCaja", codigoCaja);
        model.addAttribute("nombreCaja", nombreCaja);
        model.addAttribute("cajero", cajero);
        model.addAttribute("fechaApertura", sesion.getFechaApertura());
        model.addAttribute("fechaCierre", sesion.getFechaCierre());
        model.addAttribute("ventasTarjeta", ventasTarjeta);
        model.addAttribute("totalTarjeta", totalTarjeta);
        model.addAttribute("ventasEfectivo", ventasEfectivo);
        model.addAttribute("totalEfectivo", totalEfectivo);
        model.addAttribute("totalGeneral", totalGeneral);

        return "caja/caja-reporte";
    }

    // --- Endpoint para imprimir voucher de apertura de caja ---
    @GetMapping("/voucher-apertura/{sesionId}")
    public String imprimirVoucherApertura(@org.springframework.web.bind.annotation.PathVariable Long sesionId, Model model) {
        CajaSesionEntity sesion = sesionService.obtener(sesionId).orElse(null);
        if (sesion == null) {
            model.addAttribute("error", "Sesión no encontrada");
            return "caja/caja-sesion";
        }

        String local = sesion.getCaja().getLocal() != null ? sesion.getCaja().getLocal().getNombre() : "";
        String codigoCaja = sesion.getCaja().getCodigo();
        String nombreCaja = sesion.getCaja().getNombre();
        String cajero = sesion.getUsuarioApertura() != null ? sesion.getUsuarioApertura().getUsername() : "";

        model.addAttribute("local", local);
        model.addAttribute("codigoCaja", codigoCaja);
        model.addAttribute("nombreCaja", nombreCaja);
        model.addAttribute("cajero", cajero);
        model.addAttribute("fechaApertura", sesion.getFechaApertura());
        model.addAttribute("montoInicial", sesion.getMontoInicial());

        return "caja/voucher-apertura";
    }

       // --- AGREGADO: Cierre de caja con arqueo ---
    // --- AGREGADO: Cierre de caja con arqueo ---
@PostMapping("/cerrar-sesion")
public String cerrarSesionCaja(@RequestParam Long cajaSesionId,
                               @RequestParam double montoFisico,
                               @RequestParam(required = false) String observaciones,
                               @RequestParam("voucher_usuario") String usuarioCierre,
                               RedirectAttributes redirectAttributes) {
    // Obtener la sesión de caja
    CajaSesionEntity sesion = cajaSesionService.obtenerPorId(cajaSesionId);

    // Calcular el monto inicial y el total en efectivo
    BigDecimal montoInicial = sesion.getMontoInicial() != null ? BigDecimal.valueOf(sesion.getMontoInicial()) : BigDecimal.ZERO;
    BigDecimal totalEfectivo = cajaVentaRepository.totalVentasEfectivoPorSesion(sesion.getId());
    if (totalEfectivo == null) totalEfectivo = BigDecimal.ZERO;

    // Calcular el monto esperado por el sistema
    BigDecimal montoSistema = montoInicial.add(totalEfectivo);

    // Calcular diferencia
    BigDecimal diferencia = BigDecimal.valueOf(montoFisico).subtract(montoSistema);

    // Registrar arqueo
    ArqueoCajaEntity arqueo = new ArqueoCajaEntity();
    arqueo.setCajaSesion(sesion);
    arqueo.setFechaArqueo(LocalDateTime.now());
    arqueo.setMontoSistema(montoSistema.doubleValue());
    arqueo.setMontoFisico(montoFisico);
    arqueo.setDiferencia(diferencia.doubleValue());
    arqueo.setObservaciones(observaciones);
    arqueo.setUsuarioArqueo(usuarioCierre);
    arqueoCajaService.guardar(arqueo);

    // Setear usuario de cierre
    UsuarioEntity usuarioSesion = usuarioRepository.getUserByUsername(usuarioCierre);
    sesion.setUsuarioCierre(usuarioSesion);

    // Cerrar la sesión de caja
    sesion.setMontoCierre(montoFisico);
    sesion.setFechaCierre(LocalDateTime.now());
    sesion.setEstado("CERRADA");
    cajaSesionService.guardar(sesion);

    redirectAttributes.addFlashAttribute("exito", "Caja cerrada y arqueo registrado correctamente.");
    return "redirect:/caja";
}
}