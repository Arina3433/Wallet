package org.example.wallet.api.errors;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final ServerErrorCode serverErrorCode;

    public ServerException(ServerErrorCode serverErrorCode) {
        super(serverErrorCode.getErrorMessage());
        this.serverErrorCode = serverErrorCode;
    }

    public ServerException(ServerErrorCode serverErrorCode, String params) {
        super(String.format(serverErrorCode.getErrorMessage(), params));
        this.serverErrorCode = serverErrorCode;
    }
}
