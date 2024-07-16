//if application is not available show message
function showNotAvailableStatus(stateValue) {
    const resultDiv = document.getElementById('result');
    if (stateValue === 'NOT_AVAILABLE') {
        button = document.getElementById("button")
        button.disabled = true;
        button.style.backgroundColor = 'grey;'
        resultDiv.innerHTML = '<img src="/images/repair.png" alt="Under maintenance">';
        resultDiv.innerHTML += '<p>The service is being updated now. We are going to <s>drink beer</s> make our application better. Will be available soon!</p>';
    }
}

//add listener to handle problem with application availability
document.addEventListener('DOMContentLoaded', function() {
    fetch('/status')
        .then(response => response.json())
        .then(data => showNotAvailableStatus(data.stateValue))
        .catch(error => showNotAvailableStatus('NOT_AVAILABLE'));
});

function validatePhoneNumber(phoneNumber) {
    const phoneNumberPattern = /^ ?\+?[0-9]+ ?$/;
    return phoneNumberPattern.test(phoneNumber);
}

function showValidationError() {
    const phoneNumberInput = document.getElementById('phoneNumber');
    phoneNumberInput.style.outlineColor = 'red';
    phoneNumberInput.style.borderColor = 'red';
}

function clearValidationError() {
    const phoneNumberInput = document.getElementById('phoneNumber');
    phoneNumberInput.style.outlineColor = '';
    phoneNumberInput.style.borderColor = '';
}

//used when response returned an error
function handleServerError() {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '<img src="/images/wrong.png" alt="Internal error">';
    resultDiv.innerHTML += '<p>Sorry, something went wrong. Please, try again later.</p>';
}

//send GET request to find a country by phone number
function findCountryCode() {
    const phoneNumber = document.getElementById('phoneNumber').value;

    clearValidationError();
    if (!validatePhoneNumber(phoneNumber)) {
        showValidationError();
        return;
    }

    const trimmedNumber = phoneNumber.replace(' ', '').replace('+', '')
    const url = `/countryCode?number=${trimmedNumber}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error();
            }
            return response.json();
        })
        .then(data => {
            const resultDiv = document.getElementById('result');
            resultDiv.innerHTML = '';

            if (data.countryCodes && data.countryCodes.length > 0) {
                const ul = document.createElement('ul');

                data.countryCodes.forEach(countryCode => {
                    const li = document.createElement('li');
                    li.textContent = `code: ${countryCode.code}, country: ${countryCode.country}`;
                    ul.appendChild(li);
                });
                resultDiv.appendChild(ul);
            } else {
                resultDiv.innerHTML = '<img src="/images/ufo.png" alt="Internal error">';
                resultDiv.innerHTML += '<p>We did not find any result. </p> <p>Maybe, this phone number is from the other side of the universe.</p>';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            handleServerError();
        });
}

function handleInput() {
    const phoneNumber = document.getElementById('phoneNumber').value;
    if (validatePhoneNumber(phoneNumber)) {
        clearValidationError();
    } else {
        showValidationError();
    }
}

const phoneNumberInput = document.getElementById('phoneNumber');
if (phoneNumberInput) {
    phoneNumberInput.addEventListener('input', handleInput);
}