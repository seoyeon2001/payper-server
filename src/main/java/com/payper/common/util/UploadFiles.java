package com.payper.common.util;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public class UploadFiles {
    public static String upload(String baseDir, MultipartFile file) throws IOException {
        //기본 디렉터리가 있는지 확인, 없으면 생성
        File base = new File(baseDir);
        if(!base.exists()) base.mkdirs(); //중간 디렉터리까지 생성

        String fileName = file.getOriginalFilename();
        File dest = new File(baseDir, UploadFileName.getUniqueName(fileName));
        file.transferTo(dest); //지정 경로로 업로드 파일 이동
        return dest.getPath(); //저장 파일 경로 리턴
    }

    public static String getFormatSize(Long size) {
        if (size <= 0) return "0";
        final String[] units = new String[] { "Bytes", "KB", "MB", "GB", "TB" };
        //  1KB = 1024 Bytes, 1MB = 1024 * 1024 Bytes
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024)); //1024의 몇제곱인지 내림
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void download(HttpServletResponse response, File file, String orgName) throws Exception {
        //응답 MIME 타입을 다운로드용으로 지정 -> 파일 형식을 지정해서 브라우저가 다운로드로 인식하게 만듦
        response.setContentType("application/download");
        response.setContentLength((int)file.length());

        String filename = URLEncoder.encode(orgName, "UTF-8"); // 한글 파일명인 경우 인코딩 필수

        //다운로드 방식과 파일명을 브라우저에 지시
        response.setHeader("Content-disposition", "attachment;filename=\"" + filename + "\"");

        try(OutputStream os = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os)) {
            Files.copy(Paths.get(file.getPath()), bos);
        }
    }

    //view 동작
    public static void downloadImage(HttpServletResponse response, File file) {
        try{
            Path path = Path.of(file.getPath());
            String mimeType = Files.probeContentType(path);
            response.setContentType(mimeType);
            response.setContentLength((int) file.length());

            try (OutputStream os = response.getOutputStream() ;
                 BufferedOutputStream bos = new BufferedOutputStream(os)
            ) {
                Files.copy(path, bos); //실제 파일 내용을 응답 스트림에 복사
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
