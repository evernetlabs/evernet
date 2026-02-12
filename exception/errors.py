class ClientError(Exception):
    pass

class ServerError(Exception):
    pass

class AuthenticationError(Exception):
    pass

class AuthorizationError(Exception):
    pass

class NotFoundError(Exception):
    pass