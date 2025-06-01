document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8080/api/v1/alunos'; // Your Spring Boot API URL

    // --- Elements ---
    const addAlunoForm = document.getElementById('addAlunoForm');
    const alunoNameInput = document.getElementById('alunoName');
    const alunoNumberInput = document.getElementById('alunoNumber');
    const addAlunoMessage = document.getElementById('addAlunoMessage');

    const alunosTableBody = document.querySelector('#alunosTable tbody');
    const refreshStudentsButton = document.getElementById('refreshStudents');
    const listAlunoMessage = document.getElementById('listAlunoMessage');

    const attendanceDateInput = document.getElementById('attendanceDate');
    const startAttendanceButton = document.getElementById('startAttendance');
    const attendanceListDiv = document.getElementById('attendanceList');
    const currentAttendanceDateSpan = document.getElementById('currentAttendanceDate');
    const attendanceTableBody = document.querySelector('#attendanceTable tbody');
    const attendanceForm = document.getElementById('attendanceForm');
    const attendanceMessage = document.getElementById('attendanceMessage');

    // Set today's date as default for attendance
    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0'); // Months start at 0!
    const dd = String(today.getDate()).padStart(2, '0');
    attendanceDateInput.value = `${yyyy}-${mm}-${dd}`;


    // --- Functions ---

    // Generic message display
    function showMessage(element, message, type) {
        element.textContent = message;
        element.className = `message ${type}`;
        setTimeout(() => {
            element.textContent = '';
            element.className = 'message';
        }, 5000); // Clear message after 5 seconds
    }

    // Load students into the table
    async function loadAlunos() {
        alunosTableBody.innerHTML = '<tr><td colspan="4">Carregando alunos...</td></tr>';
        listAlunoMessage.textContent = '';
        try {
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }
            const alunos = await response.json();
            alunosTableBody.innerHTML = ''; // Clear existing rows

            if (alunos.length === 0) {
                alunosTableBody.innerHTML = '<tr><td colspan="4">Nenhum aluno registrado.</td></tr>';
                return;
            }

            alunos.forEach(aluno => {
                const row = alunosTableBody.insertRow();
                row.insertCell(0).textContent = aluno.id;
                row.insertCell(1).textContent = aluno.number;
                row.insertCell(2).textContent = aluno.name;
                const actionsCell = row.insertCell(3);
                actionsCell.className = 'action-buttons';

                // Edit Button (Placeholder - requires more complex logic for actual editing)
                const editButton = document.createElement('button');
                editButton.textContent = 'Editar';
                editButton.onclick = () => alert('Funcionalidade de edição ainda não implementada.'); // For simplicity
                actionsCell.appendChild(editButton);

                // Delete Button
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'delete';
                deleteButton.onclick = async () => {
                    if (confirm(`Tem certeza que deseja excluir o aluno ${aluno.name}?`)) {
                        try {
                            const deleteResponse = await fetch(`${API_BASE_URL}/${aluno.id}`, {
                                method: 'DELETE'
                            });
                            if (!deleteResponse.ok) {
                                const errorText = await deleteResponse.text();
                                throw new Error(`Erro ao excluir: ${deleteResponse.status}, ${errorText}`);
                            }
                            showMessage(listAlunoMessage, 'Aluno excluído com sucesso!', 'success');
                            loadAlunos(); // Reload the list
                        } catch (error) {
                            console.error('Erro ao excluir aluno:', error);
                            showMessage(listAlunoMessage, `Erro ao excluir aluno: ${error.message}`, 'error');
                        }
                    }
                };
                actionsCell.appendChild(deleteButton);
            });
        } catch (error) {
            console.error('Erro ao carregar alunos:', error);
            showMessage(listAlunoMessage, `Erro ao carregar alunos: ${error.message}`, 'error');
            alunosTableBody.innerHTML = '<tr><td colspan="4">Falha ao carregar alunos.</td></tr>';
        }
    }

    // --- Event Listeners ---

    // Add new student form submission
    addAlunoForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Prevent default form submission

        const name = alunoNameInput.value.trim();
        const number = parseInt(alunoNumberInput.value.trim());

        if (!name || isNaN(number)) {
            showMessage(addAlunoMessage, 'Por favor, preencha nome e número válidos.', 'error');
            return;
        }

        const newAluno = { name, number };

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newAluno)
            });

            if (response.status === 409) { // Conflict (e.g., number already taken)
                const errorMessage = await response.text();
                throw new Error(errorMessage);
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }

            showMessage(addAlunoMessage, 'Aluno adicionado com sucesso!', 'success');
            addAlunoForm.reset(); // Clear the form
            loadAlunos(); // Reload the student list
        } catch (error) {
            console.error('Erro ao adicionar aluno:', error);
            showMessage(addAlunoMessage, `Erro ao adicionar aluno: ${error.message}`, 'error');
        }
    });

    // Refresh students button
    refreshStudentsButton.addEventListener('click', loadAlunos);


    // Start attendance button
    startAttendanceButton.addEventListener('click', async () => {
        const attendanceDate = attendanceDateInput.value;
        if (!attendanceDate) {
            showMessage(attendanceMessage, 'Por favor, selecione uma data para a chamada.', 'error');
            return;
        }

        currentAttendanceDateSpan.textContent = new Date(attendanceDate).toLocaleDateString('pt-BR');
        attendanceTableBody.innerHTML = ''; // Clear previous attendance list
        attendanceMessage.textContent = ''; // Clear previous messages

        try {
            const response = await fetch(API_BASE_URL); // Get all students
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
            }
            const alunos = await response.json();

            if (alunos.length === 0) {
                attendanceTableBody.innerHTML = '<tr><td colspan="3">Nenhum aluno para a chamada.</td></tr>';
                attendanceListDiv.style.display = 'block';
                return;
            }

            alunos.sort((a, b) => a.number - b.number); // Sort by number for calling list

            alunos.forEach(aluno => {
                const row = attendanceTableBody.insertRow();
                row.insertCell(0).textContent = aluno.number;
                row.insertCell(1).textContent = aluno.name;
                const checkboxCell = row.insertCell(2);

                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.name = 'presentStudent';
                checkbox.value = aluno.id; // Use student ID as value
                checkbox.id = `present-${aluno.id}`;
                checkbox.checked = true; // Default to present, user unchecks if absent

                const label = document.createElement('label');
                label.htmlFor = `present-${aluno.id}`;
                label.textContent = 'Presente'; // You might not need this label for a simple checkbox

                checkboxCell.appendChild(checkbox);
                // checkboxCell.appendChild(label); // If you want the text label next to the checkbox
            });

            attendanceListDiv.style.display = 'block'; // Show the attendance section
        } catch (error) {
            console.error('Erro ao preparar chamada:', error);
            showMessage(attendanceMessage, `Erro ao preparar chamada: ${error.message}`, 'error');
            attendanceListDiv.style.display = 'none'; // Hide if error
        }
    });

    // Attendance form submission (this will need a backend endpoint for attendance)
    attendanceForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const attendanceDate = attendanceDateInput.value;
        if (!attendanceDate) {
            showMessage(attendanceMessage, 'Erro: Data da chamada não definida.', 'error');
            return;
        }

        const presentAlunoIds = Array.from(attendanceTableBody.querySelectorAll('input[name="presentStudent"]:checked'))
            .map(checkbox => parseInt(checkbox.value));
        const allAlunoIds = Array.from(attendanceTableBody.querySelectorAll('input[name="presentStudent"]'))
            .map(checkbox => parseInt(checkbox.value));

        // For a simple demo, we'll just log this.
        // In a real application, you would send this data to a *new* Spring Boot API endpoint
        // that handles saving attendance for a specific date.
        console.log('Dados da Chamada para ' + attendanceDate + ':');
        console.log('Alunos presentes (IDs):', presentAlunoIds);
        console.log('Todos os alunos (IDs):', allAlunoIds);

        // --- IMPORTANT: Backend for attendance is missing ---
        // You would need a new entity (e.g., 'Attendance'), a new repository,
        // a new service, and a new controller endpoint in Spring Boot to handle
        // saving this attendance data.
        // Example structure:
        /*
        const attendanceData = {
            date: attendanceDate,
            presentStudentIds: presentAlunoIds,
            absentStudentIds: allAlunoIds.filter(id => !presentAlunoIds.includes(id))
        };
        try {
            const response = await fetch('http://localhost:8080/api/v1/attendance', { // NEW ENDPOINT
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(attendanceData)
            });
            if (!response.ok) {
                throw new Error('Falha ao salvar chamada.');
            }
            showMessage(attendanceMessage, 'Chamada salva com sucesso!', 'success');
        } catch (error) {
            console.error('Erro ao salvar chamada:', error);
            showMessage(attendanceMessage, `Erro ao salvar chamada: ${error.message}`, 'error');
        }
        */

        showMessage(attendanceMessage, 'Chamada processada (necessita de backend para salvar).', 'success');
    });


    // --- Initial Load ---
    loadAlunos();
});