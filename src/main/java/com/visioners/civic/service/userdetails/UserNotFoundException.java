package com.visioners.civic.service.userdetails;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
        super(message);
    }
}
