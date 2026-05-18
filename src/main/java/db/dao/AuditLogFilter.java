package db.dao;

import java.time.Instant;

public class AuditLogFilter {
    public Integer userId;
    public String action;
    public Boolean success;
    public String ipAddress;

    public Instant from;
    public Instant to;

    public Integer limit = 100;
    public Integer offset = 0;
}