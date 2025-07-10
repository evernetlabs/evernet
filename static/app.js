$.ajaxSetup({
    statusCode: {
        401: function(jqXHR, textStatus, errorThrown) {
            window.location.href = "/";
        }
    }
});

function showToast(message, type = 'success') {
    const toastId = `toast-${Date.now()}`;
    const bgClass = type === 'success' ? 'bg-success text-white' : 'bg-danger text-white';

    const toastHTML = `
        <div id="${toastId}" class="toast align-items-center ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true" data-bs-delay="3000">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    const container = document.getElementById('toast-container');
    if (!container) {
        $('body').append('<div id="toast-container" class="toast-container position-fixed bottom-0 end-0 p-3"></div>');
    }

    $('#toast-container').append(toastHTML);

    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();

    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

function displaySuccess(message) {
    showToast(message, 'success');
}

function displayError(message) {
    showToast(message, 'error');
}
