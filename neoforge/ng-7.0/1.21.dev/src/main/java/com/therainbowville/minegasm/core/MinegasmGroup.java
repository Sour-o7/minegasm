package com.therainbowville.minegasm.core;

import java.util.ArrayList;

public class MinegasmGroup {
    public final static int MAX_GROUP_MEMBERS = 20;

    private String name;
    private String password;
    MinegasmConfigGroup configSettings;

    private ArrayList<MinegasmGroupMember> players;

}