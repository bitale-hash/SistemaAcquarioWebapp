// Contenuto di: src/main/webapp/js/graficiDashboard.js

function inizializzaGrafici(datiGrafici) {
    
    // --- PRIMO GRAFICO: CLIMA & CHIMICA DELL'ACQUA ---
    var ctxClima = document.getElementById('chartSaluteAcquario').getContext('2d');
    new Chart(ctxClima, {
        type: 'line',
        data: {
            labels: datiGrafici.labelsAcquario,
            datasets: [
                {
                    label: 'Temperatura (°C)',
                    data: datiGrafici.datiTemp,
                    borderColor: '#dc3545',
                    backgroundColor: 'transparent',
                    yAxisID: 'y-temp',
                    tension: 0.2
                },
                {
                    label: 'Valore pH',
                    data: datiGrafici.datiPh,
                    borderColor: '#0d6efd',
                    backgroundColor: 'transparent',
                    yAxisID: 'y-ph',
                    tension: 0.2
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    type: 'category',
                    grid: { drawOnChartArea: false }
                },
                'y-temp': { 
                    type: 'linear',
                    position: 'left',
                    title: { display: true, text: 'Temperatura (°C)' },
                    suggestedMin: 10,
                    suggestedMax: 40
                },
                'y-ph': {
                    type: 'linear',
                    position: 'right',
                    title: { display: true, text: 'pH' },
                    grid: { drawOnChartArea: false },
                    suggestedMin: 5,
                    suggestedMax: 10, 
                    ticks: { stepSize: 0.5 }
                }
            }
        }
    });

    // --- SECONDO GRAFICO: MONITORAGGIO LUCE & IDRATAZIONE ---
    var ctxLuceHidra = document.getElementById('chartLuceIdratazione').getContext('2d');
    new Chart(ctxLuceHidra, {
        data: {
            labels: datiGrafici.labelsSerra, 
            datasets: [
                {
                    type: 'line', 
                    label: 'Luce Solare (mA prodotti)',
                    data: datiGrafici.datiLuce,
                    borderColor: '#ffc107',
                    backgroundColor: 'transparent',
                    tension: 0.2,
                    yAxisID: 'y-luce'
                },
                {
                    type: 'bar', 
                    label: 'Acqua Consumata (Litri)',
                    data: datiGrafici.datiAcqua,
                    backgroundColor: 'rgba(25, 135, 84, 0.5)', 
                    borderColor: '#198754',
                    borderWidth: 1,
                    yAxisID: 'y-acqua'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    type: 'category',
                    grid: { drawOnChartArea: false }
                },
                'y-luce': {
                    type: 'linear',
                    position: 'left',
                    title: { display: true, text: 'Corrente Pannello (mA)' },
                    suggestedMin: 0,
                    suggestedMax: 2500,
                    ticks: { stepSize: 500 }
                },
                'y-acqua': {
                    type: 'linear',
                    position: 'right',
                    title: { display: true, text: 'Acqua Consumata (Litri)' },
                    grid: { drawOnChartArea: false },
                    suggestedMin: 0,
                    suggestedMax: 1000, 
                    ticks: { stepSize: 100 }
                }
            }
        }
    });
}