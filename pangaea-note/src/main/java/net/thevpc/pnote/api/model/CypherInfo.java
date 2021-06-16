/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.api.model;

import java.util.Objects;

/**
 *
 * @author vpc
 */
public class CypherInfo {

    private String algo;
    private String value;

    public CypherInfo() {
    }

    public CypherInfo(CypherInfo other) {
        if (other != null) {
            this.algo = other.algo;
            this.value = other.value;
        }
    }

    public CypherInfo(String algo, String value) {
        this.algo = algo;
        this.value = value;
    }

    public String getAlgo() {
        return algo;
    }

    public CypherInfo setAlgo(String algo) {
        this.algo = algo;
        return this;
    }

    public String getValue() {
        return value;
    }

    public CypherInfo setValue(String value) {
        this.value = value;
        return this;
    }

    public CypherInfo copy() {
        return new CypherInfo(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.algo);
        hash = 53 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CypherInfo other = (CypherInfo) obj;
        if (!Objects.equals(this.algo, other.algo)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
    
}
