package com.d20chaos.api;

public interface ID20ActionEvent {
    String getId();
    String getName();
    ActionCategory getCategory();
    DiceTier getTier();
    boolean execute(D20EventContext ctx);
}
