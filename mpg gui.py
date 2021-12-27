#Name: Ayaan Patel
#Course: COSC 1437
#Description: This is a GUI program that calculates miles per gallon.

import tkinter


class MPG:
    def __init__(self):
        # create the main window
        self.main_window = tkinter.Tk()

        # Create 4 frames to be packed on top of each other in the window
        self.gallons_frame = tkinter.Frame(self.main_window)
        self.miles_frame = tkinter.Frame(self.main_window)
        self.answer_frame = tkinter.Frame(self.main_window)
        self.button_frame = tkinter.Frame(self.main_window)

        # Create and pack the label and entry widgets for gallons
        self.gallons_label = tkinter.Label(self.gallons_frame, text="Enter Gallons:")
        self.gallons_entry = tkinter.Entry(self.gallons_frame, width=10)
        self.gallons_label.pack(side="left")
        self.gallons_entry.pack(side="left")

        # Create and pack the label and entry widgets for miles
        self.miles_label = tkinter.Label(self.miles_frame, text="Enter Miles:")
        self.miles_entry = tkinter.Entry(self.miles_frame, width=10)
        self.miles_label.pack(side="left")
        self.miles_entry.pack(side="left")

        # Create and pack the widgets for answer
        self.result_label = tkinter.Label(self.answer_frame, text='MPG:')
        self.answer = tkinter.StringVar()
        self.answer_label = tkinter.Label(self.answer_frame, textvariable=self.answer)
        self.result_label.pack(side='left')
        self.answer_label.pack(side='left')

        # Create and pack the button widgets.
        self.calc_button = tkinter.Button(self.button_frame, text='Calculate', command=self.calc_mpg)
        self.quit_button = tkinter.Button(self.button_frame, text='Quit', command=self.main_window.destroy)

        self.calc_button.pack(side='left')
        self.quit_button.pack(side='left')

        # Pack the frames
        self.gallons_frame.pack()
        self.miles_frame.pack()
        self.answer_frame.pack()
        self.button_frame.pack()

        # Start the main loop
        tkinter.mainloop()

    # The calc_mpg method is the callback function for the calc_button widget.
    def calc_mpg(self):
        # get the gallons and miles and store them in varialbes
        self.gallons = float(self.gallons_entry.get())
        self.miles = float(self.miles_entry.get())

        # Caculate the mpg
        self.mpg = self.miles / self.gallons

        # update the answer_label widget by storing the value of self.mpg in the StringVar
        # object referenced by answer.
        self.answer.set(self.mpg)


# Create an instance of the MPG class

calc_mpg = MPG()
