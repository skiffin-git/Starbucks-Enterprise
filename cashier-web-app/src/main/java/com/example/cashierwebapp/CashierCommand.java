package com.example.cashierwebapp;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class CashierCommand {

    private String action ;
    private String message ;
    private String register ;
    private String drink ;
    private String milk ;
    private String size ;

}

