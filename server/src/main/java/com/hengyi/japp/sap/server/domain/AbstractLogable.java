package com.hengyi.japp.sap.server.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by jzb on 17-7-11.
 */
@MappedSuperclass
public class AbstractLogable implements Serializable {
    @NotNull
    @ManyToOne
    @JoinColumn(updatable = false)
    protected Operator creator;
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JoinColumn(updatable = false)
    protected Date createDateTime;
    @ManyToOne
    protected Operator modifier;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifyDateTime;

    public void _log(Operator operator) {
        if (getCreator() == null) {
            setCreator(operator);
            setCreateDateTime(new Date());
        }
        setModifier(operator);
        setModifyDateTime(new Date());
    }

    public Operator getCreator() {
        return creator;
    }

    public void setCreator(Operator creator) {
        this.creator = creator;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Operator getModifier() {
        return modifier;
    }

    public void setModifier(Operator modifier) {
        this.modifier = modifier;
    }

    public Date getModifyDateTime() {
        return modifyDateTime;
    }

    public void setModifyDateTime(Date modifyDateTime) {
        this.modifyDateTime = modifyDateTime;
    }

}
