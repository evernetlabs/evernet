from flask import Flask


class HealthCheckAPI:
    
    def __init__(self, app: Flask) -> None:
        self.app = app
       
    def register(self):

        @self.app.get("/health")
        def health_check():
            return {
                "status": "ok"
            }
