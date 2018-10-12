/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.ensure.deepsea.dataobjects.folder;

/**
 *
 * @author Andy.Ward
 */
public class Policy extends BaseObject {
    
    private String policyNumber;

    /**
     * @return the policyNumber
     */
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * @param policyNumber the policyNumber to set
     */
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    
}
