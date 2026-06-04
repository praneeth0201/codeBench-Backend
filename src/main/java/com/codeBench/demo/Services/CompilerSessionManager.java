package com.codeBench.demo.Services;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CompilerSessionManager {
    private final Map<String, Process> sessions =
            new ConcurrentHashMap<>();

    public void addSession(
            String sessionId,
            Process process
    ) {
        sessions.put(sessionId, process);
    }

    public Process getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
