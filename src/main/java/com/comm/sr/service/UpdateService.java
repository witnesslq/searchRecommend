/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.comm.sr.service;

import java.util.Map;

/**
 *
 * @author jasstion
 */
public interface UpdateService {
    public void update(Map<String, String> updateMap);
    public void add(Map<String, String> updateMap);
    public void delete(Map<String, String> updateMap);

    
}
