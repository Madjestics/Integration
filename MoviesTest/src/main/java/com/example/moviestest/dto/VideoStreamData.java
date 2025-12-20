package com.example.moviestest.dto;

import java.io.InputStream;

public record VideoStreamData(InputStream inputStream, long fileLength, long start, long end, long contentLength) {
}
