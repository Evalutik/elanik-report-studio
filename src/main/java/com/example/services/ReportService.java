package com.example.services;

import com.example.models.ElementData;
import com.example.models.Measurement;
import com.example.models.Report;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.utils.Formatter.formatDateTime;

/**
 * ReportService - builds HTML from a FreeMarker template and renders it to PDF.
 *
 * High-level flow:
 *  1) buildTemplateModel(...)  - turn Report+Measurements into a Map model for FreeMarker
 *  2) renderTemplateToHtml(...) - run FreeMarker template -> HTML string
 *  3) renderPdfFromHtml(...)    - convert HTML -> PDF using OpenHTMLToPDF (optionally embed TTF font)
 *
 * The public façade is generate(Report), which wires the steps.
 */
public final class ReportService {

    // FreeMarker configuration (class-based template loading from /templates on classpath)
    private static final Configuration FM_CFG = new Configuration(Configuration.VERSION_2_3_31);

    static {
        FM_CFG.setClassForTemplateLoading(ReportService.class, "/templates");
        FM_CFG.setDefaultEncoding("UTF-8");
        FM_CFG.setOutputEncoding("UTF-8"); // add this line
        FM_CFG.setOutputFormat(HTMLOutputFormat.INSTANCE);


    }

    private ReportService() { /* no instances */ }

    /**
     * Public entry point.
     * Builds model -> HTML -> PDF file at report.outputFile().
     *
     * @param report report descriptor (measurements, target file, serial, creation date/time)
     * @throws Exception on templating / IO / PDF errors
     */
    public static void generate(Report report) throws Exception {
        // 1) convert domain objects into a free-marker friendly map
        Map<String, Object> model = buildTemplateModel(report);

        // 2) render FreeMarker template -> HTML
        String html = renderTemplateToHtml("report-template.ftl", model);

        Path debug = Paths.get("target", "debug-report.html");
        Files.createDirectories(debug.getParent());
        Files.writeString(debug, html, StandardCharsets.UTF_8);

        // 3) convert HTML -> PDF and save
        renderPdfFromHtml(html, report.outputFile());
    }

    // ---------- Step 1: Build model for FreeMarker ----------

    /**
     * Builds a Map model that FreeMarker can iterate over.
     *
     * How it works:
     *  - FreeMarker expects a basic Map/List/String/Number/Date structure.
     *  - We therefore convert each Measurement and ElementData into plain maps and lists
     *    instead of passing JavaFX properties or complicated objects.
     *
     * Why this is useful:
     *  - Simpler debugging of the rendered HTML.
     *  - Template logic remains simple (<#list measurements as m> …).
     */
    private static Map<String, Object> buildTemplateModel(Report report) throws IOException {
        Map<String, Object> model = new HashMap<>();

        model.put("logoBase64", getImageBase64("/imgs/company-logo.png"));
        model.put("creationDateTime", formatDateTime(report.creationDateTime().toString()));
        model.put("serial", report.serial() == null ? "" : report.serial());

        List<Map<String, Object>> measurements = new ArrayList<>(report.measurements().size());
        for (Measurement m : report.measurements()) {
            Map<String, Object> mm = new HashMap<>();
            mm.put("id", m.getId());
            mm.put("dateTime", m.getDateTime());
            mm.put("pointsNum", m.getPointsNum());
            mm.put("baseElementName", safe(m.getBaseElementName()));
            mm.put("alloyType", safe(m.getAlloyType()));
            mm.put("ce", m.getCE() == null ? "" : m.getCE().toString());

            mm.put("comment", safe(m.getComment()));

            mm.put("alloyNames", m.getAlloyNames());

            List<Map<String, Object>> elems = getMappedElementsData(m);
            mm.put("elementsData", elems);
            measurements.add(mm);
        }
        model.put("measurements", measurements);
        return model;
    }

    private static List<Map<String, Object>> getMappedElementsData(Measurement m) {
        List<Map<String, Object>> elems = new ArrayList<>(m.getElementsData().size());
        for (ElementData e : m.getElementsData()) {
            Map<String, Object> em = new HashMap<>();
            // put primitive values that templates can render/format
            em.put("name", e.getName());
            em.put("concentration", e.getConcentration());
            em.put("deviation", e.getDeviation());
            em.put("alloyType1", safe(e.getAlloyType1()));
            em.put("alloyType2", safe(e.getAlloyType2()));
            em.put("alloyType3", safe(e.getAlloyType3()));
            elems.add(em);
        }
        return elems;
    }

    // ---------- Step 2: Render template to HTML ----------

    /**
     * Renders the named FreeMarker template with the given model and returns HTML as a string.
     *
     * How it works:
     *  - FreeMarker loads the template from classpath (/templates/report-template.ftl).
     *  - The template sees the keys we put into the model (creationDateTime, measurements, ...).
     *  - Template.process(...) writes the filled HTML into a StringWriter which we return.
     *
     * Tips:
     *  - keep template small and let it loop over model.measurements to generate arbitrary number of tables.
     *  - debug by writing the returned HTML to a file and opening it in a browser.
     */
    private static String renderTemplateToHtml(String templateName, Map<String, Object> model)
            throws IOException, TemplateException {
        Template tpl = FM_CFG.getTemplate(templateName);
        StringWriter out = new StringWriter();
        tpl.process(model, out);
        return out.toString();
    }

    // ---------- Step 3: Convert HTML -> PDF ----------

    /**
     * Renders HTML to a PDF file using OpenHTMLToPDF (PdfRendererBuilder).
     *
     * How it works:
     *  - builder.withHtmlContent(html, baseUri) tells the renderer the HTML content. baseUri is null
     *    (resources referenced by absolute classpath URLs must be inlined or resolved manually).
     *  - useFont(...) registers a TTF to support UTF-8 scripts (Cyrillic). If you don't embed a font,
     *    output will be limited to built-in PDF fonts (Latin-only).
     *  - builder.toStream(os) + builder.run() writes the resulting PDF to the OutputStream.
     *
     * Notes on fonts:
     *  - We attempt to load "/fonts/DejaVuSans.ttf" from resources. Put a real TTF there if you need Cyrillic.
     *  - openhtmltopdf expects a File or InputStream; here we copy resource to a temp File and pass it to builder.useFont().
     */
    private static void renderPdfFromHtml(String html, File outputFile) throws Exception {
        try (OutputStream os = new FileOutputStream(outputFile)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);

            // Try to embed a TTF from resources to support Cyrillic/UTF-8
            registerDefaultFontIfAvailable(builder, "/fonts/DejaVuSans.ttf", "DejaVu Sans");
            registerDefaultFontIfAvailable(builder, "/fonts/Roboto-Regular.ttf", "Roboto-Regular");


            builder.toStream(os);
            builder.run();
        }
    }

    /**
     * If a font resource exists in the classpath, copy it to a temporary file and register with the builder.
     *
     * Why: openhtmltopdf requires a File (or streams in some overloads) and specifying font family ensures CSS font-family matches.
     */
    private static void registerDefaultFontIfAvailable(PdfRendererBuilder builder, String resourcePath, String familyName) {
        try (InputStream fontIs = ReportService.class.getResourceAsStream(resourcePath)) {
            System.out.println("fontIs " + fontIs);
            if (fontIs == null) return;

            // copy to temp file because builder.useFont(File, ...) is straightforward and reliable
            File tmp = File.createTempFile("report-font-", ".ttf");
            tmp.deleteOnExit();
            try (OutputStream fos = new FileOutputStream(tmp)) {
                fontIs.transferTo(fos);
            }

            // register the font for normal weight; if you need bold/italic, call useFont with appropriate style
            builder.useFont(tmp, familyName, 400, PdfRendererBuilder.FontStyle.NORMAL, true);
        } catch (Exception e) {
            // font is optional — swallow exception and let rendering continue with fallback fonts
            e.printStackTrace();
        }
    }

    private static String getImageBase64(String resourcePath) throws IOException {
        try (InputStream is = ReportService.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("Image not found: " + resourcePath);
            }
            byte[] imageBytes = is.readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        }
    }


    // ---------- small helpers ----------

    private static String safe(String s) {
        if (s == null) return "";
        return s;
    }





}
