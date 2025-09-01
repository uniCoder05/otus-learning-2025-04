window.onload = getAllClients();

function createClient(event) {
            event.preventDefault();
            const form = event.currentTarget;
            const phonesArr = form.phones.value.split(',').map(phoneNumber => phoneNumber.trim());
            const client = {
                name: form.name.value,
                address: form.address.value,
                phones: phonesArr
            };
            console.log(client);
            const clientStr = JSON.stringify(client);
            fetch('api/clients', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: clientStr
            }).then(getAllClients);
}

function getAllClients() {
    fetch('api/clients')
        .then(response => response.json())
        .then(refreshClientsTable);
}

function refreshClientsTable(data) {
    console.log(data);
    const tbl = document.getElementById('clientsTable');
    let html = "";
    data.forEach(client => {
        let tds = "";
        let values = Object.values(client);
        for(val of values) {
            tds+="<td>" + val + "</td>";
        }
        html += "<tr>" + tds + "</tr>";
    });
    tbl.tBodies[0].innerHTML = html;
}