package com.cibertec.pos_system.controller;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping("/reportes")
public class ReporteController {
    
    @Autowired
    private DataSource dataSource;

    /*public ReporteController(DataSource dataSource) {
        this.dataSource = dataSource;
    }*/

    @GetMapping("/reporte")
    public void generarReporte(HttpServletResponse response) throws JRException, IOException, SQLException{
        //Cargar la plantilla del reporte
        try {
        InputStream reporteStream = getClass().getResourceAsStream("/reportes/Reporte.jasper");

        Map<String, Object> params= new HashMap<>();

        //params.put("Titulo", "Ema Prueba Reporte");
        //params.put("id","1");

        JasperPrint jPrint = JasperFillManager.fillReport(reporteStream,params,dataSource.getConnection());

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=reporte-personas.pdf");

        JasperExportManager.exportReportToPdfStream(jPrint,response.getOutputStream());
        response.getOutputStream().flush();
        response.getOutputStream().close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar el reporte:" + e.getMessage());
        }
  
    }
}
