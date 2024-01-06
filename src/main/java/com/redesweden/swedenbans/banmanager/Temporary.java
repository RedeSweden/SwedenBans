package com.redesweden.swedenbans.banmanager;

public interface Temporary
{
    long getExpires();
    
    boolean hasExpired();
}
