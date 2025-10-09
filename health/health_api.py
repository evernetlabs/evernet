import flask


class HealthAPI:
    def __init__(self, app: flask.Flask):
        self.app = app

    def register(self):

        @self.app.get("/health")
        def health_check():
            return {
                "status": "ok"
            }
