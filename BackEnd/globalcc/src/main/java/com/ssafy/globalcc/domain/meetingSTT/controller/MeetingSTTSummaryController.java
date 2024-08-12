package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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

    public String createMeetingSTTSummary(ByteArrayOutputStream outputStream) throws FileNotFoundException, DocumentException {
        String filePath = "example.pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // 폰트 설정
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.DARK_GRAY);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

//        // 페이지 헤더
//        HeaderFooter header = new HeaderFooter(new Phrase("Meeting Minutes", titleFont), false);
//        header.setAlignment(Element.ALIGN_CENTER);
//        document.setHeader(header);
//
//        // 페이지 푸터
//        HeaderFooter footer = new HeaderFooter(new Phrase("Page ", normalFont), true);
//        footer.setAlignment(Element.ALIGN_CENTER);
//        document.setFooter(footer);

        // 제목 추가
        document.add(new Paragraph("Meeting Minutes", titleFont));
        document.add(new Paragraph("Meeting ID: " + 1, normalFont));
        document.add(new Paragraph("Date: " + LocalDate.now(), normalFont));
        document.add(Chunk.NEWLINE);

        // 회의 참석자 목록
        document.add(new Paragraph("Participants:", headerFont));

        // 테이블 생성
        PdfPTable table = new PdfPTable(2); // 두 개의 열: 참석자 번호, 이름
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // 테이블 헤더
        PdfPCell cell = new PdfPCell(new Phrase("No", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Name", tableHeaderFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        table.addCell(cell);

        // 참석자 데이터 추가
        table.addCell("1");
        table.addCell("John Doe");

        table.addCell("2");
        table.addCell("Jane Smith");

        table.addCell("3");
        table.addCell("Bob Johnson");

        document.add(table);
        document.add(Chunk.NEWLINE);

        // 주요 논의 사항
        document.add(new Paragraph("Main Discussion Points:", headerFont));
        document.add(new Paragraph("1. Project timeline review", normalFont));
        document.add(new Paragraph("2. Budget allocation", normalFont));
        document.add(new Paragraph("3. Task assignments", normalFont));
        document.add(Chunk.NEWLINE);

        // 결론 및 액션 아이템
        document.add(new Paragraph("Conclusions and Action Items:", headerFont));
        document.add(new Paragraph("1. Finalize the project plan by next week", normalFont));
        document.add(new Paragraph("2. Prepare a budget report by end of month", normalFont));
        document.add(new Paragraph("3. Assign tasks to team members", normalFont));

        document.close();

        return filePath;
    }
}
