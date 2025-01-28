package com.dmj.fusioncharts.resources;

import com.fusioncharts.exporter.beans.ExportBean;
import javax.servlet.http.HttpServletResponse;

/* loaded from: FCExporter_Format.class */
public abstract class FCExporter_Format {
    public abstract Object exportProcessor(ExportBean exportBean);

    public abstract String exportOutput(Object obj, HttpServletResponse httpServletResponse);
}
