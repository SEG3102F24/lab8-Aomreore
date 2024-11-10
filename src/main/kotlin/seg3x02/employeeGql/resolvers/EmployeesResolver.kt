package seg3x02.employeeGql.resolvers

import org.springframework.stereotype.Controller
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.*

@Controller
class EmployeesResolver(private val employeeRepository: EmployeesRepository)
{
    @QueryMapping
    fun employees(): List<Employee>
    {
        return employeeRepository.findAll();
    }

    @QueryMapping
    fun employeeById(@Argument employeeId: String): Employee? 
    {
        val employee = employeeRepository.findById(employeeId)
        return employee.orElse(null)
    }

    @MutationMapping
    fun addEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee 
    {
        if (input.name != null && input.dateOfBirth != null && input.city != null && input.salary != null) 
        {
            val employee = Employee(input.name, input.dateOfBirth, input.city, input.salary, input.gender, input.email)
            employee.employeeId = UUID.randomUUID().toString()
            employeeRepository.save(employee)
            return employee
        } 
        else 
        {
            throw Exception("Invalid input")
        }
    }

    @MutationMapping
    fun deleteEmployee(@Argument("employeeId") id: String) : Boolean 
    {
        employeeRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(@Argument employeeId: String, @Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee 
    {
        val employee = employeeRepository.findById(employeeId)

        employee.ifPresent 
        {
            if (input.name != null) 
            {
                it.name = input.name
            }
            if (input.dateOfBirth != null) 
            {
                it.dateOfBirth = input.dateOfBirth
            }
            if (input.city != null) 
            {
                it.city = input.city
            }
            if (input.salary != null) 
            {
                it.salary = input.salary
            }
            if (input.gender != null) 
            {
                it.gender = input.gender
            }
            if (input.email != null) 
            {
                it.email = input.email
            }

            employeeRepository.save(it)
        }
        return employee.get()
    }

}




/////////////////////////////////////////////////////////////////////////////////////////////////////////////

data class Employee(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String?
)

@Component
class EmployeesResolver : Query, Mutation {

    private val employees = mutableListOf<Employee>() // Use a DB in production

    fun employees(): List<Employee> = employees

    fun employeeById(id: String): Employee? =
        employees.find { it.id == id }

    fun addEmployee(firstName: String, lastName: String, email: String, position: String?): Employee {
        val employee = Employee(id = generateId(), firstName, lastName, email, position)
        employees.add(employee)
        return employee
    }

    fun updateEmployee(id: String, firstName: String?, lastName: String?, email: String?, position: String?): Employee? {
        val employee = employees.find { it.id == id } ?: return null
        employee.firstName = firstName ?: employee.firstName
        employee.lastName = lastName ?: employee.lastName
        employee.email = email ?: employee.email
        employee.position = position ?: employee.position
        return employee
    }

    fun deleteEmployee(id: String): Boolean {
        return employees.removeIf { it.id == id }
    }

    private fun generateId(): String = employees.size.toString()
}
