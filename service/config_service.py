class ConfigService:
    def __init__(self):
        pass

    def get_jwt_signing_key(self):
        return "secret"

    def get_vertex_endpoint(self):
        return "localhost:3000"