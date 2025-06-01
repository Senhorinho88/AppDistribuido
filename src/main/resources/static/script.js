document.addEventListener('DOMContentLoaded', () => {
    /* ------------------------------------------------------------
     *  Endpoints REST
     * ---------------------------------------------------------- */
    const API_ALUNOS     = 'http://localhost:8080/api/v1/alunos';
    const API_PRESENCAS  = 'http://localhost:8080/api/presencas';

    /* ------------------------------------------------------------
     *  Elementos da página
     * ---------------------------------------------------------- */
    // Cadastro de aluno
    const addAlunoForm      = document.getElementById('addAlunoForm');
    const alunoNameInput    = document.getElementById('alunoName');
    const alunoNumberInput  = document.getElementById('alunoNumber');
    const addAlunoMessage   = document.getElementById('addAlunoMessage');

    // Tabela de alunos
    const alunosTableBody   = document.querySelector('#alunosTable tbody');
    const refreshBtn        = document.getElementById('refreshStudents');
    const listAlunoMessage  = document.getElementById('listAlunoMessage');

    // Chamada
    const attendanceDateInput  = document.getElementById('attendanceDate');
    const startAttendanceBtn   = document.getElementById('startAttendance');
    const attendanceListDiv    = document.getElementById('attendanceList');
    const currentDateSpan      = document.getElementById('currentAttendanceDate');
    const attendanceTableBody  = document.querySelector('#attendanceTable tbody');
    const attendanceForm       = document.getElementById('attendanceForm');
    const attendanceMessage    = document.getElementById('attendanceMessage');

    /* ------------------------------------------------------------
     *  Utilidades
     * ---------------------------------------------------------- */
    function showMessage(element, text, type) {
        element.textContent = text;
        element.className = `message ${type}`;
        setTimeout(() => {
            element.textContent = '';
            element.className = 'message';
        }, 5000);
    }

    /* ------------------------------------------------------------
     *  Carregar alunos
     * ---------------------------------------------------------- */
    async function loadAlunos() {
        alunosTableBody.innerHTML = '<tr><td colspan="4">Carregando...</td></tr>';
        listAlunoMessage.textContent = '';

        try {
            const res = await fetch(API_ALUNOS);
            if (!res.ok) throw new Error(await res.text());

            const alunos = await res.json();
            alunosTableBody.innerHTML = '';

            if (!alunos.length) {
                alunosTableBody.innerHTML =
                    '<tr><td colspan="4">Nenhum aluno registrado.</td></tr>';
                return;
            }

            alunos.forEach(a => {
                const row = alunosTableBody.insertRow();
                row.insertCell(0).textContent = a.id;
                row.insertCell(1).textContent = a.number;
                row.insertCell(2).textContent = a.name;

                const actions = row.insertCell(3);
                actions.className = 'action-buttons';

                // Botão Excluir
                const del = document.createElement('button');
                del.textContent = 'Excluir';
                del.className = 'delete';
                del.onclick = () => deleteAluno(a);
                actions.appendChild(del);
            });
        } catch (err) {
            console.error(err);
            showMessage(listAlunoMessage, `Erro ao carregar alunos: ${err.message}`, 'error');
            alunosTableBody.innerHTML =
                '<tr><td colspan="4">Falha ao carregar alunos.</td></tr>';
        }
    }

    async function deleteAluno(aluno) {
        if (!confirm(`Excluir o aluno ${aluno.name}?`)) return;
        try {
            const res = await fetch(`${API_ALUNOS}/${aluno.id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error(await res.text());

            showMessage(listAlunoMessage, 'Aluno excluído!', 'success');
            loadAlunos();
        } catch (err) {
            console.error(err);
            showMessage(listAlunoMessage, `Erro: ${err.message}`, 'error');
        }
    }

    /* ------------------------------------------------------------
     *  Evento: Cadastrar aluno
     * ---------------------------------------------------------- */
    addAlunoForm.addEventListener('submit', async e => {
        e.preventDefault();

        const name   = alunoNameInput.value.trim();
        const number = parseInt(alunoNumberInput.value.trim(), 10);

        if (!name || Number.isNaN(number)) {
            showMessage(addAlunoMessage, 'Nome e número são obrigatórios.', 'error');
            return;
        }

        try {
            const res = await fetch(API_ALUNOS, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, number })
            });
            if (!res.ok) throw new Error(await res.text());

            showMessage(addAlunoMessage, 'Aluno cadastrado!', 'success');
            addAlunoForm.reset();
            loadAlunos();
        } catch (err) {
            console.error(err);
            showMessage(addAlunoMessage, `Erro: ${err.message}`, 'error');
        }
    });

    /* ------------------------------------------------------------
     *  Evento: Listar alunos na chamada
     * ---------------------------------------------------------- */
    startAttendanceBtn.addEventListener('click', async () => {
        const dateStr = attendanceDateInput.value;
        if (!dateStr) {
            showMessage(attendanceMessage, 'Selecione uma data.', 'error');
            return;
        }

        currentDateSpan.textContent =
            new Date(dateStr).toLocaleDateString('pt-BR', { timeZone: 'UTC' });
        attendanceTableBody.innerHTML = '';
        attendanceMessage.textContent = '';

        try {
            const res = await fetch(API_ALUNOS);
            if (!res.ok) throw new Error(await res.text());

            const alunos = await res.json();
            if (!alunos.length) {
                attendanceTableBody.innerHTML =
                    '<tr><td colspan="3">Nenhum aluno para chamada.</td></tr>';
                attendanceListDiv.style.display = 'block';
                return;
            }

            alunos.sort((a, b) => a.number - b.number);
            alunos.forEach(a => {
                const row = attendanceTableBody.insertRow();
                row.insertCell(0).textContent = a.number;
                row.insertCell(1).textContent = a.name;

                const cb = document.createElement('input');
                cb.type  = 'checkbox';
                cb.value = a.id;
                cb.checked = true;

                const cell = row.insertCell(2);
                cell.appendChild(cb);
            });

            attendanceListDiv.style.display = 'block';
        } catch (err) {
            console.error(err);
            showMessage(attendanceMessage, `Erro: ${err.message}`, 'error');
            attendanceListDiv.style.display = 'none';
        }
    });

    /* ------------------------------------------------------------
     *  Evento: Salvar chamada
     * ---------------------------------------------------------- */
    attendanceForm.addEventListener('submit', async e => {
        e.preventDefault();

        const dateStr = attendanceDateInput.value;
        if (!dateStr) {
            showMessage(attendanceMessage, 'Data não definida.', 'error');
            return;
        }

        const presentIds = Array.from(
            attendanceTableBody.querySelectorAll('input:checked')
        ).map(cb => parseInt(cb.value, 10));

        const allIds = Array.from(
            attendanceTableBody.querySelectorAll('input')
        ).map(cb => parseInt(cb.value, 10));

        /* ---------- Salvar via API Presenças ---------- */
        try {
            const isToday =
                dateStr === new Date().toISOString().substring(0, 10); // yyyy-MM-dd

            const requests = allIds.map(id => {
                const presente = presentIds.includes(id);

                if (!presente) return Promise.resolve(); // ausente -> ignorado

                // Presente: usa /marcar ou /marcar-data
                if (isToday) {
                    return fetch(`${API_PRESENCAS}/marcar/${id}`, {
                        method: 'POST'
                    });
                } else {
                    const body = {
                        alunoId: id,
                        dataHora: `${dateStr}T00:00:00`
                    };
                    return fetch(`${API_PRESENCAS}/marcar-data`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(body)
                    });
                }
            });

            const results = await Promise.all(requests);
            const failed  = results.filter(r => r && !r.ok);

            if (failed.length) {
                const txt = await failed[0].text();
                throw new Error(`Falha em ${failed.length} chamadas: ${txt}`);
            }

            showMessage(attendanceMessage, 'Chamada salva!', 'success');
        } catch (err) {
            console.error(err);
            showMessage(attendanceMessage, `Erro ao salvar chamada: ${err.message}`, 'error');
        }
    });

    /* ------------------------------------------------------------
     *  Inicialização
     * ---------------------------------------------------------- */
    refreshBtn.addEventListener('click', loadAlunos);

    // Define hoje como valor padrão da data
    attendanceDateInput.value = new Date().toISOString().substring(0, 10);

    loadAlunos();
});
