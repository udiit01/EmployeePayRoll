document.addEventListener("DOMContentLoaded", () => {
    const fetchBtn = document.querySelector(".fetch-btn");
    const addBtn = document.querySelector(".add-btn");
    const generateBtn = document.querySelector(".generate-btn");
    const employeeList = document.getElementById("employee-list");
    const payslipOutput = document.getElementById("payslip-output");

    // Handle Full-Time / Part-Time Toggle
    document.getElementById("employeeType").addEventListener("change", (event) => {
        const type = event.target.value;
        document.querySelectorAll(".ft-fields").forEach(el => el.classList.toggle("hidden", type !== "FULLTIME"));
        document.querySelectorAll(".pt-fields").forEach(el => el.classList.toggle("hidden", type !== "PARTTIME"));
    });

    // Add Employee
    addBtn.addEventListener("click", async () => {
        const type = document.getElementById("employeeType").value;
        const name = document.getElementById("empName").value;
        const monthlySalary = document.getElementById("monthlySalary").value;
        const hoursWorked = document.getElementById("hoursWorked").value;
        const hourlyRate = document.getElementById("hourlyRate").value;

        let employeeData = { employeeType: type, name };
        if (type === "FULLTIME") employeeData.monthlySalary = monthlySalary;
        else employeeData.hoursWorked = hoursWorked, employeeData.hourlyRate = hourlyRate;

        const response = await fetch(`${config.API_BASE_URL}/employees`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(employeeData)
        });

        if (response.ok) alert("Employee added successfully!");
        else alert("Error adding employee.");
    });

    // Fetch Employees
    fetchBtn.addEventListener("click", async () => {
        employeeList.innerHTML = "<p>Loading...</p>";

        try {
            const response = await fetch(`${config.API_BASE_URL}/employees`);
            const employees = await response.json();

            console.log("API Response:", employees);

            employeeList.innerHTML = ""; // Clear previous data

            if (!Array.isArray(employees) || employees.length === 0) {
                employeeList.innerHTML = "<p style='color:yellow;'>No employees found.</p>";
                console.warn("No employees to display.");
                return;
            }

            employees.forEach(emp => {
                if (!emp.name || !emp.id) {
                    console.warn("Skipping invalid employee:", emp);
                    return;
                }

                console.log(`Rendering Employee: ${emp.name}, ID: ${emp.id}`);

                const empCard = document.createElement("div");
                empCard.classList.add("card");
                empCard.innerHTML = `
                    <h3>${emp.name}</h3>
                    <p><strong>ID:</strong> ${emp.id}</p>
                    <p><strong>Type:</strong> ${emp.employeeType || "N/A"}</p>
                    <p><strong>Salary:</strong> ${emp.monthlySalary ? `₹${emp.monthlySalary}` : "N/A"}</p>
                `;

                employeeList.appendChild(empCard);
            });

            console.log("Employee list updated in UI.");
        } catch (error) {
            console.error("Fetch Error:", error);
            employeeList.innerHTML = "<p style='color:red;'>Failed to load employees.</p>";
        }
    });

    // Generate Payslip
    generateBtn.addEventListener("click", async () => {
        console.log("Generate Payslip button clicked!");

        const empId = document.getElementById("empId").value;
        const bonus = document.getElementById("bonus").value;
        const hours = document.getElementById("hours").value;

        if (!empId || !bonus || !hours) {
            payslipOutput.innerHTML = "<p style='color:red;'>All fields are required!</p>";
            return;
        }

        payslipOutput.innerHTML = "<p>Generating payslip...</p>";

        try {
            const response = await fetch(`${config.API_BASE_URL}/payslip/${empId}?bonus=${bonus}&totalHoursWorked=${hours}`, {
                method: "POST",
                headers: { "Content-Type": "application/json" }
            });

            if (!response.ok) throw new Error("Payslip generation failed!");

            const payslip = await response.json();
            payslipOutput.innerHTML = `
                <h3>Payslip for ${payslip.employee.name}</h3>
                <p><strong>Basic Salary:</strong> ₹${payslip.basicSalary}</p>
                <p><strong>Bonus:</strong> ₹${payslip.bonus}</p>
                <p><strong>Overtime Pay:</strong> ₹${payslip.overtimePay}</p>
                <p><strong>Net Salary:</strong> ₹${payslip.netSalary}</p>
                <p><strong>Tax Deduction:</strong> ₹${payslip.taxDeduction}</p>
                <p><strong>Insurance Deduction:</strong> ₹${payslip.insuranceDeduction}</p>
                <p><strong>Provident Fund:</strong> ₹${payslip.providentFund}</p>
                <p><strong>Total Deductions:</strong> ₹${payslip.totalDeductions}</p>



            `;
        } catch (error) {
            console.error("Error generating payslip:", error);
            payslipOutput.innerHTML = "<p style='color:red;'>Failed to generate payslip.</p>";
        }
    });
});



