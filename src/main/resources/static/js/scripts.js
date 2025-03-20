// Base URL for API
const baseUrl = 'http://localhost:8080/api';

// Function to show/hide sections
function showSection(sectionId) {
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => section.classList.add('hidden'));

    const selectedSection = document.getElementById(sectionId);
    if (selectedSection) selectedSection.classList.remove('hidden');
    console.log('Section displayed:', sectionId);

    const responseDiv = document.getElementById('response');
    const tableContainer = document.getElementById('tableContainer');
    const messageContainer = document.getElementById('messageContainer');
    if (responseDiv && tableContainer && messageContainer) {
        responseDiv.classList.add('hidden');
        tableContainer.innerHTML = '';
        messageContainer.getElementsByTagName('pre')[0].textContent = '';
        console.log('Response area reset');
    }
}

// Function to display response
function displayResponse(message) {
    const responseArea = document.getElementById('response');
    const messageContainer = document.getElementById('messageContainer');
    const pre = messageContainer ? messageContainer.getElementsByTagName('pre')[0] : null;
    if (responseArea && messageContainer && pre) {
        pre.textContent = message;
        responseArea.classList.remove('hidden');
        messageContainer.classList.remove('hidden');
        console.log('Message displayed:', message);
    } else {
        console.error('Response area, message container, or pre element not found:', {
            responseArea: !!responseArea,
            messageContainer: !!messageContainer,
            pre: !!pre
        });
    }
}

// Hàm lấy danh sách xe và điền vào dropdown
async function populateCarDropdown() {
    try {
        const response = await fetch(`${baseUrl}/cars`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const cars = await response.json();
        const carSelect = document.getElementById('createCarId');

        carSelect.innerHTML = '<option value="">Select a car</option>';

        cars.forEach(car => {
            const option = document.createElement('option');
            option.value = car.id;
            option.textContent = `${car.category?.name || 'Unknown'} (${car.license_plate}) - $${car.price_per_day}/day`;
            carSelect.appendChild(option);
        });
    } catch (error) {
        console.error('Error fetching cars:', error);
        displayResponse('Error: Could not load car list');
    }
}

// Gọi populateCarDropdown khi trang được tải
document.addEventListener('DOMContentLoaded', () => {
    const submitReturnButton = document.getElementById('submitReturn');
    if (submitReturnButton) {
        submitReturnButton.addEventListener('click', returnCar);
    } else {
        console.error('Submit Return button not found');
    }

    const submitCancelButton = document.getElementById('submitCancel');
    if (submitCancelButton) {
        submitCancelButton.addEventListener('click', cancelBooking);
    } else {
        console.error('Submit Cancel button not found');
    }

    const carIdInput = document.getElementById('createCarId');
    const startDateInput = document.getElementById('createStartDate');
    const endDateInput = document.getElementById('createEndDate');

    if (carIdInput && startDateInput && endDateInput) {
        carIdInput.addEventListener('change', updateTotalAmountDisplay);
        startDateInput.addEventListener('change', updateTotalAmountDisplay);
        endDateInput.addEventListener('change', updateTotalAmountDisplay);
    } else {
        console.error('One or more input fields for Total Amount display not found');
    }

    populateCarDropdown(); // Gọi hàm để điền danh sách xe
});

// Hàm lấy pricePerDay từ API dựa trên carId
async function fetchPricePerDay(carId) {
    try {
        const response = await fetch(`${baseUrl}/cars/${carId}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const car = await response.json();
        if (!car.price_per_day) {
            throw new Error('Price per day not available for this car');
        }
        return car.price_per_day;
    } catch (error) {
        console.error('Error fetching car price:', error);
        displayResponse(`Error: Could not fetch car price for Car ID ${carId}. ${error.message}`);
        return 0;
    }
}

// Hàm tính số ngày giữa startDate và endDate
function calculateDays(startDateStr, endDateStr) {
    const startDate = new Date(startDateStr);
    const endDate = new Date(endDateStr);

    const timeDiff = endDate - startDate;
    const daysDiff = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
    return daysDiff > 0 ? daysDiff : 0;
}

// Hàm cập nhật Total Amount để hiển thị
async function updateTotalAmountDisplay() {
    const carId = document.getElementById('createCarId').value;
    const startDate = document.getElementById('createStartDate').value;
    const endDate = document.getElementById('createEndDate').value;
    const totalAmountDisplay = document.getElementById('totalAmountDisplay');

    if (carId && startDate && endDate) {
        const pricePerDay = await fetchPricePerDay(carId);
        if (pricePerDay === 0) {
            totalAmountDisplay.textContent = 'Error: Could not fetch car price';
            return;
        }

        const days = calculateDays(startDate, endDate);
        if (days === 0) {
            totalAmountDisplay.textContent = 'Error: Invalid date range';
            return;
        }

        const totalAmount = pricePerDay * days;
        totalAmountDisplay.textContent = `$${totalAmount.toFixed(2)}`;
    } else {
        totalAmountDisplay.textContent = '-';
    }
}

// Function to create a booking
async function createBooking() {
    const customerId = parseInt(document.getElementById('createCustomerId').value);
    const employeeId = parseInt(document.getElementById('createEmployeeId').value);
    const carId = parseInt(document.getElementById('createCarId').value);
    const startDate = document.getElementById('createStartDate').value;
    const endDate = document.getElementById('createEndDate').value;
    const status = document.getElementById('createStatus').value;
    const discountCode = document.getElementById('createDiscountCode').value;

    console.log('Debug - Input values:', { customerId, employeeId, carId, startDate, endDate, status, discountCode });

    if (!customerId || !employeeId || !carId || !startDate || !endDate || !status) {
        displayResponse('Error: All required fields must be filled');
        return;
    }
    if (new Date(startDate) >= new Date(endDate)) {
        displayResponse('Error: Start date must be before end date');
        return;
    }

    try {
        const response = await fetch(`${baseUrl}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                car_id: carId,
                customer_id: customerId,
                employee_id: employeeId,
                start_date: startDate,
                end_date: endDate,
                status: status,
                discount_code: discountCode || null
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const data = await response.json();
        const message = `Booking created successfully!\n` +
            `ID: ${data.id || 'undefined'}\n` +
            `Customer: ${data.customer?.name || data.customer_id || 'undefined'}\n` +
            `Employee: ${data.employee?.name || data.employee_id || 'undefined'}\n` +
            `Car: ${data.car?.category?.name || 'Unknown'} (${data.car?.license_plate || data.car_id || 'undefined'})\n` +
            `Start Date: ${data.start_date || 'undefined'}\n` +
            `End Date: ${data.end_date || 'undefined'}\n` +
            `Status: ${data.status || 'undefined'}\n` +
            `Total Amount: ${data.total_amount || 'undefined'}\n` +
            `Discount Code: ${data.discount_code || 'None'}`;
        displayResponse(message);

        document.getElementById('createCustomerId').value = '';
        document.getElementById('createEmployeeId').value = '';
        document.getElementById('createCarId').value = '';
        document.getElementById('createStartDate').value = '';
        document.getElementById('createEndDate').value = '';
        document.getElementById('createStatus').value = 'pending';
        document.getElementById('createDiscountCode').value = '';
        document.getElementById('totalAmountDisplay').textContent = '-';
    } catch (error) {
        displayResponse(`Error: ${error.message}`);
    }
}

// Function to pick up a car
async function pickUpCar() {
    const bookingId = parseInt(document.getElementById('pickUpBookingId').value);

    if (!bookingId) {
        displayResponse('Error: Booking ID is required');
        return;
    }

    try {
        const response = await fetch(`${baseUrl}/bookings/${bookingId}/pickup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const data = await response.json();
        console.log('Debug - Pickup response:', data);

        const message = `Car picked up successfully!\n` +
            `ID: ${data.id || 'undefined'}\n` +
            `Customer: ${data.customer?.name || data.customer_id || 'undefined'}\n` +
            `Employee: ${data.employee?.name || data.employee_id || 'undefined'}\n` +
            `Car: ${data.car?.category?.name || 'Unknown'} (${data.car?.license_plate || data.car_id || 'undefined'})\n` +
            `Start Date: ${data.start_date || 'undefined'}\n` +
            `End Date: ${data.end_date || 'undefined'}\n` +
            `Status: ${data.status || 'undefined'}\n` +
            `Total Amount: ${data.total_amount || 'undefined'}\n` +
            `Discount Code: ${data.discount_code || 'None'}`;
        displayResponse(message);

        document.getElementById('pickUpBookingId').value = '';
    } catch (error) {
        displayResponse(`Error: ${error.message}`);
    }
}

// Function to return a car
async function returnCar() {
    const bookingId = parseInt(document.getElementById('returnBookingId').value.trim());
    const returnDateInput = document.getElementById('returnDate').value.trim();
    const carCondition = document.getElementById('carCondition').value;
    const additionalFees = document.getElementById('additionalFees').value;

    if (!bookingId || !returnDateInput || !carCondition) {
        displayResponse('Error: Booking ID, Return Date, and Car Condition are required');
        return;
    }

    console.log('Debug - Raw returnDateInput:', returnDateInput);
    const isoReturnDate = returnDateInput;
    console.log('Debug - isoReturnDate:', isoReturnDate);

    try {
        const response = await fetch(`${baseUrl}/bookings/${bookingId}/return`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                return_date: isoReturnDate,
                car_condition: carCondition,
                additional_fees: additionalFees || 0
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const data = await response.json();
        console.log('Debug - Return response:', data);

        const message = `Car returned successfully!\n` +
            `ID: ${data.id || 'undefined'}\n` +
            `Customer: ${data.customer?.name || data.customer_id || 'undefined'}\n` +
            `Employee: ${data.employee?.name || data.employee_id || 'undefined'}\n` +
            `Car: ${data.car?.category?.name || 'Unknown'} (${data.car?.license_plate || data.car_id || 'undefined'})\n` +
            `Start Date: ${data.start_date || 'undefined'}\n` +
            `End Date: ${data.end_date || 'undefined'}\n` +
            `Return Date: ${data.return_date || 'undefined'}\n` +
            `Status: ${data.status || 'undefined'}\n` +
            `Total Amount: ${data.total_amount || 'undefined'}\n` +
            `Additional Fees: ${data.additional_fees || '0'}\n` +
            `Discount Code: ${data.discount_code || 'None'}\n` +
            `Car Condition: ${data.car_condition || 'undefined'}`;
        displayResponse(message);

        document.getElementById('returnBookingId').value = '';
        document.getElementById('returnDate').value = '';
        document.getElementById('carCondition').value = 'Good';
        document.getElementById('additionalFees').value = '';
    } catch (error) {
        displayResponse(`Error: ${error.message}`);
    }
}

// Function to cancel a booking
async function cancelBooking() {
    const bookingIdInput = document.getElementById('cancelBookingId').value.trim();
    const bookingId = parseInt(bookingIdInput);

    console.log('Debug - Raw bookingIdInput:', bookingIdInput);
    console.log('Debug - Parsed bookingId:', bookingId);

    if (!bookingIdInput || isNaN(bookingId) || bookingId <= 0) {
        displayResponse('Error: Please enter a valid Booking ID (positive number)');
        return;
    }

    try {
        const response = await fetch(`${baseUrl}/bookings/${bookingId}/cancel`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        console.log('Debug - Response status:', response.status);
        console.log('Debug - Content-Type:', response.headers.get('content-type'));

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const contentType = response.headers.get('content-type');
        let data;
        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
            console.log('Debug - Cancel response (JSON):', data);
        } else {
            const text = await response.text();
            console.log('Debug - Cancel response (Text):', text);
            data = { message: text };
        }

        const message = `Booking cancelled successfully!\n` +
            `ID: ${data.id || bookingId}\n` +
            `Customer: ${data.customer?.name || data.customer_id || 'undefined'}\n` +
            `Employee: ${data.employee?.name || data.employee_id || 'undefined'}\n` +
            `Car: ${data.car?.category?.name || 'Unknown'} (${data.car?.license_plate || data.car_id || 'undefined'})\n` +
            `Status: ${data.status || 'cancelled'}\n` +
            `Start Date: ${data.start_date || 'undefined'}\n` +
            `End Date: ${data.end_date || 'undefined'}`;
        displayResponse(message);

        document.getElementById('cancelBookingId').value = '';
    } catch (error) {
        displayResponse(`Error: ${error.message}`);
    }
}

// Function to process payment
async function processPayment() {
    const bookingId = parseInt(document.getElementById('paymentBookingId').value);
    const paymentDate = document.getElementById('paymentDate').value;
    let method = document.getElementById('paymentMethod').value;
    let status = 'COMPLETED';

    if (method === 'credit_card') {
        method = 'CREDIT_CARD';
    }

    if (!bookingId || !paymentDate || !method) {
        displayResponse('Error: All required fields must be filled');
        return;
    }

    try {
        const response = await fetch(`${baseUrl}/payments/process`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ booking_id: bookingId, payment_date: paymentDate, method: method, status: status })
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const data = await response.json();
        const message = `Payment processed successfully!\n` +
            `Payment ID: ${data.id}\n` +
            `Booking ID: ${data.booking_id}\n` +
            `Amount: ${data.amount}\n` +
            `Method: ${data.method}\n` +
            `Status: ${data.status}\n` +
            `Payment Date: ${data.payment_date}`;
        displayResponse(message);

        document.getElementById('paymentBookingId').value = '';
        document.getElementById('paymentDate').value = '';
        document.getElementById('paymentMethod').value = 'CREDIT_CARD';
    } catch (error) {
        displayResponse(`Error: ${error.message}`);
    }
}

// Function to get picked-up bookings and display as a table
async function getPickedUpBookings() {
    try {
        console.log('Fetching picked-up bookings...');
        const response = await fetch(`${baseUrl}/bookings/picked-up?status=picked_up`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        console.log('Response status:', response.status);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP error! status: ${response.status} - ${errorText || 'No additional details'}`);
        }

        const data = await response.json();
        console.log('Fetched data:', data);

        if (Array.isArray(data)) {
            console.log('Picked-Up Bookings:');
            console.table(data.map(booking => ({
                ID: booking.id,
                Customer: booking.customer?.name || booking.customer_id || 'N/A',
                Employee: booking.employee?.name || booking.employee_id || 'N/A',
                Car: booking.car?.license_plate || booking.car_id || 'N/A',
                'Start Date': booking.start_date || 'N/A',
                'End Date': booking.end_date || 'N/A',
                Status: booking.status || 'N/A',
                'Total Amount': booking.total_amount || 'N/A',
                'Discount Code': booking.discount_code || 'N/A'
            })));
        } else {
            console.log('No bookings found or invalid data:', data);
        }

        const responseArea = document.getElementById('response');
        const tableContainer = document.getElementById('tableContainer');
        const messageContainer = document.getElementById('messageContainer');
        if (responseArea && tableContainer && messageContainer) {
            console.log('DOM elements found:', { responseArea, tableContainer, messageContainer });
            if (Array.isArray(data) && data.length > 0) {
                let tableHTML = `
                    <table class="min-w-full bg-white border border-gray-300">
                        <thead>
                            <tr class="bg-gray-200 text-gray-700">
                                <th class="py-2 px-4 border-b">ID</th>
                                <th class="py-2 px-4 border-b">Customer</th>
                                <th class="py-2 px-4 border-b">Employee</th>
                                <th class="py-2 px-4 border-b">Car (License Plate)</th>
                                <th class="py-2 px-4 border-b">Start Date</th>
                                <th class="py-2 px-4 border-b">End Date</th>
                                <th class="py-2 px-4 border-b">Status</th>
                                <th class="py-2 px-4 border-b">Total Amount</th>
                                <th class="py-2 px-4 border-b">Discount Code</th>
                            </tr>
                        </thead>
                        <tbody>
                `;

                data.forEach(booking => {
                    const carInfo = booking.car && booking.car.category && booking.car.license_plate
                        ? `${booking.car.category.name} (${booking.car.license_plate})`
                        : 'N/A';

                    tableHTML += `
                        <tr class="hover:bg-gray-100">
                            <td class="py-2 px-4 border-b">${booking.id || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${booking.customer?.name || booking.customer_id || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${booking.employee?.name || booking.employee_id || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${carInfo}</td>
                            <td class="py-2 px-4 border-b">${booking.start_date || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${booking.end_date || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${booking.status || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${booking.total_amount || 'N/A'}</td>
                            <td class="py-2 px-4 border-b">${booking.discount_code || 'N/A'}</td>
                        </tr>
                    `;
                });

                tableHTML += `
                        </tbody>
                    </table>
                `;

                tableContainer.innerHTML = tableHTML;
                tableContainer.classList.remove('hidden');
                console.log('Table displayed');
            } else {
                tableContainer.innerHTML = '<p class="text-gray-600">No picked-up bookings found.</p>';
                tableContainer.classList.remove('hidden');
                console.log('No bookings message displayed');
            }
            responseArea.classList.remove('hidden');
            console.log('Response area displayed');
        } else {
            console.error('Response area, table container, or message container not found');
        }
    } catch (error) {
        console.error('Error fetching picked-up bookings:', error);
        displayResponse(`Error: ${error.message}`);
    }
}

// Add event listeners for buttons and inputs
document.addEventListener('DOMContentLoaded', () => {
    const submitReturnButton = document.getElementById('submitReturn');
    if (submitReturnButton) {
        submitReturnButton.addEventListener('click', returnCar);
    } else {
        console.error('Submit Return button not found');
    }

    const submitCancelButton = document.getElementById('submitCancel');
    if (submitCancelButton) {
        submitCancelButton.addEventListener('click', cancelBooking);
    } else {
        console.error('Submit Cancel button not found');
    }

    // Thêm event listeners để cập nhật Total Amount hiển thị
    const carIdInput = document.getElementById('createCarId');
    const startDateInput = document.getElementById('createStartDate');
    const endDateInput = document.getElementById('createEndDate');

    if (carIdInput && startDateInput && endDateInput) {
        carIdInput.addEventListener('change', updateTotalAmountDisplay);
        startDateInput.addEventListener('change', updateTotalAmountDisplay);
        endDateInput.addEventListener('change', updateTotalAmountDisplay);
    } else {
        console.error('One or more input fields for Total Amount display not found');
    }
});