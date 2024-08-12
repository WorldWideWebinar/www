package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/summary")
@RequiredArgsConstructor
public class MeetingSTTSummaryController {

    @GetMapping("/{meetingId}")
    public ResponseEntity<InputStreamResource> generateSummary(@PathVariable("meetingId") String meetingId) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        createMeetingSTTSummary(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=meeting_minutes_" + meetingId + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(inputStream));
    }

    public void createMeetingSTTSummary(ByteArrayOutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 36, 36, 72, 108); // Margins: left, right, top, bottom
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.open();

        String fontPath = "NanumSquareB.ttf";
        BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        // Fonts
        Font titleFont = new Font(baseFont, 20, Font.BOLD, BaseColor.BLUE);
        Font headerFont = new Font(baseFont, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Font normalFont = new Font(baseFont, 12, Font.NORMAL);
        Font tableHeaderFont = new Font(baseFont, 12, Font.BOLD, BaseColor.WHITE);
        Font tableNormalFont = new Font(baseFont, 12, Font.NORMAL);

        // Title
        Paragraph title = new Paragraph("회  의  록", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("날짜: " + LocalDate.now(), normalFont));
        document.add(new Paragraph("시간: " + LocalDateTime.now(), normalFont));
        document.add(Chunk.NEWLINE);

        // Participants
        Paragraph participantsHeader = new Paragraph("참여자:", headerFont);
        document.add(participantsHeader);

        PdfPTable table = new PdfPTable(2); // Two columns: No, Name
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        PdfPCell cell = new PdfPCell(new Phrase("번호", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("이름", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Adding participant data
        addTableRow(table, "1", "김수빈", tableNormalFont);
        addTableRow(table, "2", "주연수", tableNormalFont);
        addTableRow(table, "3", "이주영", tableNormalFont);

        document.add(table);
        document.add(Chunk.NEWLINE);

        // Main Discussion Points
        Paragraph discussionHeader = new Paragraph("회의 주요 안건:", headerFont);
        document.add(discussionHeader);
        document.add(new Paragraph("1. Project timeline review", normalFont));
        document.add(new Paragraph("2. Budget allocation", normalFont));
        document.add(new Paragraph("3. Task assignments", normalFont));
        document.add(Chunk.NEWLINE);

        // Conclusions and Action Items
        Paragraph conclusionsHeader = new Paragraph("회의 결론:", headerFont);
        document.add(conclusionsHeader);
        document.add(new Paragraph("1. Finalize the project plan by next week", normalFont));
        document.add(new Paragraph("2. Prepare a budget report by end of month", normalFont));
        document.add(new Paragraph("3. Assign tasks to team members", normalFont));

        document.close();

        addWatermark(new ByteArrayInputStream(outputStream.toByteArray()), outputStream);
    }

    private void addWatermark(InputStream pdfInputStream, ByteArrayOutputStream outputStream) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(pdfInputStream);
        PdfStamper stamper = new PdfStamper(reader, outputStream);
        PdfContentByte canvas;
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.3f); // Set transparency level

        int numberOfPages = reader.getNumberOfPages();

        for (int i = 1; i <= numberOfPages; i++) {
            canvas = stamper.getOverContent(i);
            canvas.setGState(gs);

            // Load the watermark image
            Image watermarkImage = Image.getInstance("logo.png");
            watermarkImage.setAbsolutePosition(150, 300); // Position (x, y) on the page
            //watermarkImage.setRotationDegrees(45); // Rotate if necessary

            // Add watermark image to the content
            canvas.addImage(watermarkImage);
        }

        stamper.close();
        reader.close();
    }

    private void addTableRow(PdfPTable table, String no, String name, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(no, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(name, font));
        table.addCell(cell);
    }
}
