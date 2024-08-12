package com.ssafy.globalcc.domain.meetingSTT.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/summary")
@RequiredArgsConstructor
public class MeetingSTTSummaryController {

    @GetMapping("/{meetingId}")
    public String generateSummary(@PathVariable("meetingId") String meetingId, Model model) {
        String filePath = createMeetingSTTSummary();

        model.addAttribute("meetingId", meetingId);
        model.addAttribute("filePath", filePath);

        return "summarytest";
    }

    public String createMeetingSTTSummary() {
        String filePath = "example.pdf";
        String content = "This is a sample content for the PDF file.";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(content));

            System.out.println("PDF 파일이 성공적으로 생성되었습니다!");

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return filePath;
    }
}
