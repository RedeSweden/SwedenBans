package com.redesweden.swedenbans.commands.bridge;

public interface Bridge
{
    void export() throws Exception;
    
    void load() throws Exception;
}
