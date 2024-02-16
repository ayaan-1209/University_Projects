


from tkinter import *

# Considering labels that are to be placed on Form
labels = ['Discussions (5% of total)', 'Quizzes (15% of total)', 'Programs/Project (15% of total)', 'MyLabs (15% of total)', 'Exam1 (10% of total)', 'Exam2 (10% of total)', 'Final Exam Exam1 (30% of total)'];

def constructForm(root, labels):
# Function that constructs form
   # Empty list
   elements = []
  
   head = True

   # Iterating over labels
   for field in labels:
       # Creating a frame
       row = Frame(root)
       if head:
           headLbl = Label(row, width=30, font=("Helvetica", 10), text='Enter the scores for the following assignments and exams', anchor=W, justify=RIGHT)
           headLbl.pack(side=TOP, fill=X)
           head = False
       # Creating a label
       currLabel = Label(row, width=30, text=field, anchor='w')
       # Creating a text box
       currentTextBox = Entry(row)
       # Placing frame
       row.pack(side=TOP, fill=X, padx=5, pady=5)
       # Adding label
       currLabel.pack(side=LEFT)
       # Adding text box
       currentTextBox.pack(side=LEFT, fill=X)
       # Adding to list
       elements.append((field, currentTextBox))
  
   # Creating another frame for results
   # Creating a frame
   row = Frame(root)
   # Creating a label
   currLabel = Label(row, width=20, text="Letter Grade: ", anchor='w')
   # Creating a text box
   #currentTextBox = Entry(row)
   letterLabel = Label(row, width=20, text="", anchor='w')
   # Placing frame
   row.pack(side=BOTTOM, fill=X, padx=5, pady=5)
   # Adding label
   currLabel.pack(side=LEFT)
   # Adding text box
   letterLabel.pack(side=RIGHT, fill=X)
          
   # Adding all elements
   elements.append(("Letter Grade: ", letterLabel));
  
   # Creating another frame for results
   # Creating a frame
   row = Frame(root)
   # Creating a label
   currLabel = Label(row, width=20, text="Average: ", anchor='w')
   # Creating a text box
   #currentTextBox = Entry(row)
   avgLabel = Label(row, width=20, text="", anchor='w')
   # Placing frame
   row.pack(side=BOTTOM, fill=X, padx=5, pady=5)
   # Adding label
   currLabel.pack(side=LEFT)
   # Adding text box
   avgLabel.pack(side=RIGHT, fill=X)
          
   # Adding all elements
   elements.append(("Average: ", avgLabel));
      
   # Return elements
   return elements
  
def findGrade(percentage):
#Function that calculates grade based on percentage
   grade = "";
   # Finding grade based on percentage
   if percentage >= 89.45:
       grade = "A";
   elif percentage >= 79.45 and percentage < 89.45:
       grade = "B";
   elif percentage >= 69.45 and percentage < 79.45:
       grade = "C";
   elif percentage >= 59.45 and percentage < 69.45:
       grade = "D";
   else:
       grade = "F";
   # Return grade
   return grade;
  
def handler(entries):
   #Handler function for Calculate button
   # Fetching required values from text boxes
   discussions = float(entries[0][1].get())
   quizzes = float(entries[1][1].get())
   programs = float(entries[2][1].get())
   MyLabs = float(entries[3][1].get())
   Exam1 = float(entries[4][1].get())
   Exam2 = float(entries[5][1].get())
   finalExam = float(entries[6][1].get())
  
   # Calculating average
   avg = (discussions*0.05) + (quizzes*0.15) + (programs*0.15) + (MyLabs*0.15) + (Exam1*0.10) + (Exam2*0.10) + (finalExam*0.30)
  
   # Finding letter grade
   grade = findGrade(avg)
  
   # Adding result to label
   entries[8][1]["text"] = str(round(avg, 2))
   entries[7][1]["text"] = grade
      

def main():
#Main function
  
   # Creating tkinter object
   root = Tk()
   # Adding Title
   root.title("Average/Letter Grade Calculator");
   # Constructing form
   entries = constructForm(root, labels)
  
   # Adding Calculate Button
   b1 = Button(root, text='Calculate Average/Letter Grade', command=(lambda e=entries: handler(e)))
   b2 = Button(root, text='Quit', command=root.destroy)
   b1.pack(side=LEFT, padx=5, pady=5)
   b2.pack(side=LEFT, padx=5, pady=5)
  
   root.mainloop()
  
# Calling main function
main();
