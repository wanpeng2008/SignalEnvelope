# -*- coding: utf-8 -*-
"""
Created on Wed Aug  2 14:52:20 2017

@author: Administrator
"""

import sys
sys.path.append('/Users/pengwan/Downloads/JyNI-2.7-alpha.4-bin-macosx-10.12-intel')
sys.path.append('/Library/Frameworks/Python.framework/Versions/2.7/lib/python2.7/site-packages')

import numpy as np

import scipy.io as sio
import scipy.signal as signal


from scipy import fftpack

def test(f_low,f_high,fs,numbertap,filePath):
    x = []
    file_object = open(filePath)
    for line in file_object:
         x.append(line)
    file_object.close()
    return spe_env(f_low,f_high,fs,numbertap,x)
def spe_env(f_low,f_high,fs,numbertap,x):
    x = np.array(x)
    xx = Filter(f_low,f_high,fs,x)
    hx = fftpack.hilbert(xx)
    x_env = np.sqrt(xx**2 + hx**2)    
    X_env = x_env - np.mean(x_env)
    N = len(X_env)
    amp = np.abs(np.fft.fft(X_env)/N*2)
    amp = amp[0:int(N/2)]
    amp = amp.tolist()
    return amp



def Filter (f_low, f_high, fs, x):
    numbertap=2001
    f1=f_low/fs*2
    f2=f_high/fs*2
    wn=signal.firwin (numbertap, [f1, f2], pass_zero=False)
    y=signal.lfilter(wn, 1.0, x)
    return y    
