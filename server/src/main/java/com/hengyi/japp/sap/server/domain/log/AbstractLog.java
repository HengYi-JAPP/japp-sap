package com.hengyi.japp.sap.server.domain.log;

import com.hengyi.japp.sap.server.domain.Operator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jzb on 17-7-11.
 */
@MappedSuperclass
public class AbstractLog implements Serializable {
    @NotNull
    @ManyToOne
    @JoinColumn(updatable = false)
    protected Operator operator;
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    protected Date startDateTime = new Date();
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date endDateTime;
    @Column(updatable = false)
    @Lob
    protected String note;
    @Id
    @Column(length = 36)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
