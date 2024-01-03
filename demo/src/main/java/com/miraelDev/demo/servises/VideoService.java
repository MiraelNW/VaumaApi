package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.VideoDbModel;
import com.miraelDev.demo.repositories.anime.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public void saveAll(Set<VideoDbModel> dbModels) {
        videoRepository.saveAll(dbModels);
    }


//    public ResponseEntity<StreamingResponseBody> playMediaV01(
//            String id,
//            String season,
//            String episode,
//            String quality,
//            String name
//    ) {
//        try {
//            StreamingResponseBody responseStream;
//            String filePathString = "D:/"+id+season+episode+quality+name;
//            Path filePath = Paths.get(filePathString);
//            Long fileSize = Files.size(filePath);
//            byte[] buffer = new byte[1024];
//            final HttpHeaders responseHeaders = new HttpHeaders();

//            if (rangeHeader == null) {
//                responseHeaders.add("Content-Type", "video/mp4");
//                responseHeaders.add("Content-Length", fileSize.toString());
//                responseStream = os -> {
//                    RandomAccessFile file = new RandomAccessFile(filePathString, "r");
//                    try (file) {
//                        long pos = 0;
//                        file.seek(pos);
//                        while (pos < fileSize - 1) {
//                            file.read(buffer);
//                            os.write(buffer);
//                            pos += buffer.length;
//                        }
//                        os.flush();
//                    } catch (Exception e) {
//                    }
//                };
//
//                return new ResponseEntity<StreamingResponseBody>
//                        (responseStream, responseHeaders, HttpStatus.OK);
//            }

//            String[] ranges = rangeHeader.split("-");
//            Long rangeStart = Long.parseLong(ranges[0].substring(6));
//            Long rangeEnd;
//            if (ranges.length > 1) {
//                rangeEnd = Long.parseLong(ranges[1]);
//            } else {
//                rangeEnd = fileSize - 1;
//            }
//
//            if (fileSize < rangeEnd) {
//                rangeEnd = fileSize - 1;
//            }

//            String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
//            responseHeaders.add("Content-Type", "video/mp4");
//            responseHeaders.add("Content-Length", contentLength);
//            responseHeaders.add("Accept-Ranges", "bytes");
//            responseHeaders.add("Content-Range", "bytes" + " " +
//                    rangeStart + "-" + rangeEnd + "/" + fileSize);
//            final Long _rangeEnd = rangeEnd;
//            responseStream = os -> {
//                RandomAccessFile file = new RandomAccessFile(filePathString, "r");
//                try (file) {
//                    long pos = rangeStart;
//                    file.seek(pos);
//                    while (pos < _rangeEnd) {
//                        file.read(buffer);
//                        os.write(buffer);
//                        pos += buffer.length;
//                    }
//                    os.flush();
//                } catch (Exception e) {
//                }
//            };
//
//            return new ResponseEntity<StreamingResponseBody>
//                    (responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
//        } catch (FileNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        } catch (IOException e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    public ResponseEntity<StreamingResponseBody> loadPartialMediaFile
            (String localMediaFilePath, String rangeValues)
            throws IOException {
        if (!StringUtils.hasText(rangeValues)) {
            System.out.println("Read all media file content.");
            return loadEntireMediaFile(localMediaFilePath);
        } else {
            long rangeStart = 0L;
            long rangeEnd = 0L;

            if (!StringUtils.hasText(localMediaFilePath)) {
                throw new IllegalArgumentException
                        ("The full path to the media file is NULL or empty.");
            }

            Path filePath = Paths.get(localMediaFilePath);
            if (!filePath.toFile().exists()) {
                throw new FileNotFoundException("The media file does not exist.");
            }

            long fileSize = Files.size(filePath);

            System.out.println("Read rang seeking value.");
            System.out.println("Rang values: [" + rangeValues + "]");

            int dashPos = rangeValues.indexOf("-");
            if (dashPos > 0 && dashPos <= (rangeValues.length() - 1)) {
                String[] rangesArr = rangeValues.split("-");

                if (rangesArr != null && rangesArr.length > 0) {
                    System.out.println("ArraySize: " + rangesArr.length);
                    if (StringUtils.hasText(rangesArr[0])) {
                        System.out.println("Rang values[0]: [" + rangesArr[0] + "]");
                        String valToParse = numericStringValue(rangesArr[0]);
                        rangeStart = safeParseStringValuetoLong(valToParse, 0L);
                    } else {
                        rangeStart = 0L;
                    }

                    if (rangesArr.length > 1) {
                        System.out.println("Rang values[1]: [" + rangesArr[1] + "]");
                        String valToParse = numericStringValue(rangesArr[1]);
                        rangeEnd = safeParseStringValuetoLong(valToParse, 0L);
                    } else {
                        if (fileSize > 0) {
                            rangeEnd = fileSize - 1L;
                        } else {
                            rangeEnd = 0L;
                        }
                    }
                }
            }

            if (rangeEnd == 0L && fileSize > 0L) {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }

            System.out.println(String.format("Parsed Range Values: [%d] - [%d]",
                    rangeStart, rangeEnd));

            return loadPartialMediaFile(localMediaFilePath, rangeStart, rangeEnd);
        }
    }

    private ResponseEntity<StreamingResponseBody>
    loadPartialMediaFile(String localMediaFilePath, long fileStartPos, long fileEndPos)
            throws IOException {
        StreamingResponseBody responseStream;
        Path filePath = Paths.get(localMediaFilePath);
        if (!filePath.toFile().exists()) {
            throw new FileNotFoundException("The media file does not exist.");
        }

        long fileSize = Files.size(filePath);
        if (fileStartPos < 0L) {
            fileStartPos = 0L;
        }

        if (fileSize > 0L) {
            if (fileStartPos >= fileSize) {
                fileStartPos = fileSize - 1L;
            }

            if (fileEndPos >= fileSize) {
                fileEndPos = fileSize - 1L;
            }
        } else {
            fileStartPos = 0L;
            fileEndPos = 0L;
        }

        byte[] buffer = new byte[1024];
        String mimeType = Files.probeContentType(filePath);

        final HttpHeaders responseHeaders = new HttpHeaders();
        String contentLength = String.valueOf((fileEndPos - fileStartPos) + 1);
        responseHeaders.add("Content-Type", mimeType);
        responseHeaders.add("Content-Length", contentLength);
        responseHeaders.add("Accept-Ranges", "bytes");
        responseHeaders.add("Content-Range",
                String.format("bytes %d-%d/%d", fileStartPos, fileEndPos, fileSize));

        final long fileStartPos2 = fileStartPos;
        final long fileEndPos2 = fileEndPos;
        responseStream = os -> {
            RandomAccessFile file = new RandomAccessFile(localMediaFilePath, "r");
            try (file) {
                long pos = fileStartPos2;
                file.seek(pos);
                while (pos < fileEndPos2) {
                    file.read(buffer);
                    os.write(buffer);
                    pos += buffer.length;
                }
                os.flush();
            } catch (Exception e) {
            }
        };

        return new ResponseEntity<StreamingResponseBody>
                (responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }

    private ResponseEntity<StreamingResponseBody> loadEntireMediaFile(String localMediaFilePath) throws IOException {
        Path filePath = Paths.get(localMediaFilePath);
        if (!filePath.toFile().exists()) {
            throw new FileNotFoundException("The media file does not exist.");
        }

        long fileSize = Files.size(filePath);
        long endPos = fileSize;
        if (fileSize > 0L) {
            endPos = fileSize - 1;
        } else {
            endPos = 0L;
        }

        ResponseEntity<StreamingResponseBody> retVal =
                loadPartialMediaFile(localMediaFilePath, 0, endPos);

        return retVal;
    }

    private long safeParseStringValuetoLong(String valToParse, long defaultVal) {
        long retVal = defaultVal;
        if (StringUtils.hasText(valToParse)) {
            try {
                retVal = Long.parseLong(valToParse);
            } catch (NumberFormatException ex) {
                retVal = defaultVal;
            }
        }

        return retVal;
    }

    private String numericStringValue(String origVal) {
        String retVal = "";
        if (StringUtils.hasText(origVal)) {
            retVal = origVal.replaceAll("[^0-9]", "");
            System.out.println("Parsed Long Int Value: [" + retVal + "]");
        }

        return retVal;
    }

}
