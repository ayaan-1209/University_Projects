#Name: Ayaan Patel.
#Course: COSC1437.
#DEscription: This program uses accessors and mutators to display details of employees using an employee class and a sub class called production worker.

class Employee(): # Employee class

    def __init__(self,name,emp_no):    #Constructor of class
        self.emp_name=name
        self.emp_num=emp_no

class ProductionWorker(Employee):    #Inheritance happened here 

    def __init__(self,name,emp_no,shift,rate): #Constructor of the ProoductionWorker class

        super().__init__(name,emp_no)
        self.shift=shift
        self.hr_rate=rate

    def setShift(self,shift):self.shift=shift
    def setHourlyRate(self,rate):self.hr_rate=rate

    def getShift(self): # This assigns day or night to the shift the employee is working 
        if self.shift==1: return 'Day Shift'
        elif self.shift==2: return 'Night Shift'
        else: return 'Please select between 1 and 2'
        return self.shift

    def getHourlyRate(self): return self.hr_rate
    def getName(self):return self.emp_name
    def getEmployeeNumber(self):return self.emp_num
    def getEmployeeRate(self):return self.emp_num


def main():

    name=input('Enter employee name: ')
    id =input('Enter employee id: ')
    shift = int(input('Enter [1] for Day shift [2] for Night shift: '))
    rate=float(input('Enter employee\'s hourly rate: '))

    prod_worker = ProductionWorker(name,id,shift,rate) #Object of the production worker class
    print('\nUsing accessor functions printing employee details - ')
  #Display values
    print('Employee Name: {}'.format(prod_worker.getName()))
    print('Employee Number: {}'.format(prod_worker.getEmployeeNumber()))
    print('Employee Shift: {}'.format(prod_worker.getShift()))
    print('Employee Hourly Wage: ${}'.format(prod_worker.getHourlyRate()))

main() #calling the main function
#Program end 