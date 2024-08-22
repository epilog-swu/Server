package com.epi.epilog.app.service.fall;


import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.epi.epilog.app.dto.PdfData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfService {
    private final TemplateEngine templateEngine;

    public ByteArrayOutputStream createPdf(String diabetesLog, List<PdfData> entries) throws Exception {
        Context context = new Context();
        context.setVariable("entries", entries);

        String htmlContent = templateEngine.process(diabetesLog, context);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        String baseUri = this.getClass().getResource("/templates/").toString();
//        renderer.setDocumentFromString(htmlContent, baseUri);

        String fontPathNanumGothic = "/templates/fonts/NanumGothic-Regular.ttf";
        String fontPathDelaGothicOne = "/templates/fonts/DelaGothicOne-Regular.ttf";
        renderer.getFontResolver().addFont(new ClassPathResource(fontPathNanumGothic).getURL().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        renderer.getFontResolver().addFont(new ClassPathResource(fontPathDelaGothicOne).getURL().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.setDocumentFromString(htmlContent, baseUri);

        renderer.layout();
        renderer.createPDF(baos);

        return baos;
    }
}
