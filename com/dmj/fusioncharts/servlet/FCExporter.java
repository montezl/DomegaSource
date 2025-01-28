package com.dmj.fusioncharts.servlet;

import com.dmj.fusioncharts.resources.FCExporter_Format;
import com.fusioncharts.exporter.ErrorHandler;
import com.fusioncharts.exporter.FusionChartsExportHelper;
import com.fusioncharts.exporter.beans.ExportBean;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* loaded from: FCExporter.class */
public class FCExporter extends HttpServlet {
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void destroy() {
        super.destroy();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = getServletContext();
        String WEB_ROOT_PATH = sc.getRealPath("/");
        ExportBean localExportBean = FusionChartsExportHelper.parseExportRequestStream(request);
        String exportFormat = (String) localExportBean.getExportParameterValue("exportformat");
        String exportTargetWindow = (String) localExportBean.getExportParameterValue("exporttargetwindow");
        StringBuffer err_warn_Codes = new StringBuffer();
        if (localExportBean.getMetadata().getWidth() == -1 || localExportBean.getMetadata().getHeight() == -1 || localExportBean.getMetadata().getWidth() == 0 || localExportBean.getMetadata().getHeight() == 0) {
            err_warn_Codes.append("E101,");
        }
        if (localExportBean.getMetadata().getBgColor() == null) {
            err_warn_Codes.append("W513,");
        }
        if (localExportBean.getStream() == null) {
            err_warn_Codes.append("E100,");
        }
        String exportAction = (String) localExportBean.getExportParameterValue("exportaction");
        boolean isHTML = false;
        if (exportAction.equals("download")) {
            isHTML = true;
        }
        if (!exportAction.equals("download")) {
            String fileNameWithoutExt = (String) localExportBean.getExportParameterValue("exportfilename");
            String extension = FusionChartsExportHelper.getExtensionFor(exportFormat.toLowerCase());
            String fileName = fileNameWithoutExt + "." + extension;
            err_warn_Codes.append(ErrorHandler.checkServerSaveStatus(WEB_ROOT_PATH, fileName));
        }
        String pathToWebAppRoot = getServletContext().getRealPath("/");
        localExportBean.addExportParameter("webapproot", pathToWebAppRoot);
        String meta_values = localExportBean.getMetadataAsQueryString((String) null, true, isHTML);
        try {
            Class exporterClass = Class.forName("com.dmj.fusioncharts.resources.FCExporter_IMG");
            FCExporter_Format fcExporter = (FCExporter_Format) exporterClass.newInstance();
            Object exportObject = fcExporter.exportProcessor(localExportBean);
            fcExporter.exportOutput(exportObject, response);
        } catch (ClassNotFoundException e) {
            err_warn_Codes.append("E404");
            writeError(response, "" + isHTML, err_warn_Codes.toString(), meta_values, exportTargetWindow);
        } catch (IllegalAccessException e2) {
            err_warn_Codes.append("E404");
            writeError(response, "" + isHTML, err_warn_Codes.toString(), meta_values, exportTargetWindow);
        } catch (InstantiationException e3) {
            err_warn_Codes.append("E404");
            writeError(response, "" + isHTML, err_warn_Codes.toString(), meta_values, exportTargetWindow);
        }
    }

    private void writeError(HttpServletResponse response, String isHTML, String errorCodes, String otherMessages, String exportTargetWindow) {
        if (isHTML == null) {
            isHTML = "true";
        }
        if (otherMessages == null) {
            otherMessages = "";
        }
        if (errorCodes == null) {
            errorCodes = "";
        }
        response.setContentType("text/html");
        if (exportTargetWindow.equalsIgnoreCase("_self")) {
            response.addHeader("Content-Disposition", "attachment;");
        } else {
            response.addHeader("Content-Disposition", "inline;");
        }
        try {
            PrintWriter out = response.getWriter();
            out.print(ErrorHandler.buildResponse(errorCodes, new Boolean(isHTML).booleanValue()));
            out.print(otherMessages);
        } catch (IOException e) {
        }
    }
}
