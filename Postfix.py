print("Postfix Calculator\n")

stack = []              # Empty stack

y = int(0)

z = int(0)

w = int(0)

while True:

   x = input("Enter a postfix expression one by one:")

   if x >= '0' and x<= '9':

      x = int(x)

      stack.append(x)      # Push on top of stack

   elif x == '+':          # Got an operator

      if len(stack) >= 2:

         y = stack.pop()   # Pop from top of stack

         z = stack.pop()

         w = z + y            #here u have to change it to operand2 operator operand1

         stack.append(w)   # Push result back

      else:

         print("Stack error") # Not enough operhand A + B

         break

   elif x == '-':

      if len(stack) >= 2:

         y = stack.pop()

         z = stack.pop()

         w = z - y      #here u have to change it to operand2 operator operand1

         stack.append(w)

      else:

         print("Stack error")

         break

   elif x == '*':

      if len(stack) >= 2:

         y = stack.pop()

         z = stack.pop()

         w = z * y              #here u have to change it to operand2 operator operand1

         stack.append(w)

      else:

         print("Stack error")

         break

   elif x == '/':

      if len(stack) >= 2:

         y = stack.pop()

         z = stack.pop()

         w = z // y    #here u have to change it to operand2 operator operand1,extra "/" operator for floor division.

         stack.append(w)

      else:

         print("Stack error")

         break

   elif x == '=':             # Equal operator

      if len(stack) == 1:

         z = stack.pop()      # Pop the result

         print (z)

         break

      else:

         print("Stack error")

         break

