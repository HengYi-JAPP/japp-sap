package com.hengyi.japp.sap;

import com.hengyi.japp.sap.grpc.server.SapSysDef;
import org.jzb.J;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class SapSystemDefine implements Serializable {
    protected DestinationType destinationType;
    protected String client;
    protected String user;
    protected String passwd;

    public SapSystemDefine() {
    }

    public SapSystemDefine(SapSysDef sysDef) {
        this.destinationType = Optional.ofNullable(sysDef.getDestType())
                .map(SapSysDef.DestType::name)
                .map(DestinationType::valueOf)
                .orElse(null);
        this.client = sysDef.getClient();
        this.user = sysDef.getUser();
        this.passwd = sysDef.getPasswd();
    }

    public DestinationType getDestinationType() {
        return Optional.ofNullable(this.destinationType).orElse(DestinationType.PRO);
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public String getClient() {
        return Optional.ofNullable(this.client)
                .filter(J::nonBlank)
                .map(J::deleteWhitespace)
                .orElse("800");
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUser() {
        return Optional.ofNullable(this.user)
                .filter(J::nonBlank)
                .map(J::deleteWhitespace)
                .map(String::toUpperCase)
                .orElse("RFC_JAPP");
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return Optional.ofNullable(this.passwd)
                .filter(J::nonBlank)
                .orElse("123456");
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SapSystemDefine that = (SapSystemDefine) o;
        return destinationType == that.destinationType &&
                Objects.equals(client, that.client) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationType, client, user);
    }
}
