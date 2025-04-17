export interface ApiErrorResponse
{
  message: string;
  code: ApiErrorCode;
  status: number;
}

export enum ApiErrorCode
{
  BAD_AUTH_TOKEN = 'BAD_AUTH_TOKEN',
  AUTH_TOKEN_EXPIRED = 'AUTH_TOKEN_EXPIRED',
  AUTH_TOKEN_MISSING = 'AUTH_TOKEN_MISSING',
  SERVER_NOT_INSTALLED = 'SERVER_NOT_INSTALLED'
}
