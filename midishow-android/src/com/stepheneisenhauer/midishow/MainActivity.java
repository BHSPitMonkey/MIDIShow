package com.stepheneisenhauer.midishow;

import java.util.List;
import java.util.Set;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener;
import jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener;
import jp.kshoji.driver.midi.listener.OnMidiInputEventListener;
import jp.kshoji.driver.midi.thread.MidiDeviceConnectionWatcher;
import jp.kshoji.driver.midi.util.Constants;
import jp.kshoji.driver.midi.util.UsbMidiDeviceUtils;
import jp.kshoji.driver.usb.util.DeviceFilter;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication implements OnMidiDeviceDetachedListener, OnMidiDeviceAttachedListener, OnMidiInputEventListener {

	UsbDevice device = null;
	UsbDeviceConnection deviceConnection = null;
	MidiInputDevice midiInputDevice = null;
	MidiOutputDevice midiOutputDevice = null;
	private MidiDeviceConnectionWatcher deviceConnectionWatcher = null;
	Handler deviceDetachedHandler = null;
	
	private OnMidiDeviceAttachedListener deviceAttachedListener = new OnMidiDeviceAttachedListener() {
		/*
		 * (non-Javadoc)
		 * @see jp.kshoji.driver.midi.listener.OnMidiDeviceAttachedListener#onDeviceAttached(android.hardware.usb.UsbDevice, android.hardware.usb.UsbInterface)
		 */
		@Override
		public synchronized void onDeviceAttached(final UsbDevice attachedDevice) {
			if (device != null) {
				// already one device has been connected
				return;
			}

			UsbManager usbManager = (UsbManager) getApplicationContext().getSystemService(Context.USB_SERVICE);
			
			deviceConnection = usbManager.openDevice(attachedDevice);
			if (deviceConnection == null) {
				return;
			}
			
			List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(getApplicationContext());

			Set<MidiInputDevice> foundDevices = UsbMidiDeviceUtils.findMidiInputDevices(attachedDevice, deviceConnection, deviceFilters, MainActivity.this);
			midiInputDevice = (MidiInputDevice) foundDevices.toArray()[0];
			
			Set<MidiOutputDevice> foundOutputDevices = UsbMidiDeviceUtils.findMidiOutputDevices(attachedDevice, deviceConnection, deviceFilters);
			midiOutputDevice = (MidiOutputDevice) foundOutputDevices.toArray()[0];
			
			Log.d(Constants.TAG, "Device " + attachedDevice.getDeviceName() + " has been attached.");
			
			MainActivity.this.onDeviceAttached(attachedDevice);
		}
	};

	private OnMidiDeviceDetachedListener deviceDetachedListener = new OnMidiDeviceDetachedListener() {
		/*
		 * (non-Javadoc)
		 * @see jp.kshoji.driver.midi.listener.OnMidiDeviceDetachedListener#onDeviceDetached(android.hardware.usb.UsbDevice)
		 */
		@Override
		public void onDeviceDetached(final UsbDevice detachedDevice) {
			// Stop input device's thread.
			if (midiInputDevice != null) {
				midiInputDevice.stop();
				midiInputDevice = null;
			}
			
			midiOutputDevice = null;

			if (deviceConnection != null) {
				List<DeviceFilter> deviceFilters = DeviceFilter.getDeviceFilters(getApplicationContext());

				Set<UsbInterface> allInterfaces = UsbMidiDeviceUtils.findAllMidiInterfaces(detachedDevice, deviceFilters);
				
				for (UsbInterface usbInterface : allInterfaces) {
					if (usbInterface != null) {
						deviceConnection.releaseInterface(usbInterface);
					}
				}
				
				deviceConnection.close();
				deviceConnection = null;
			}
			device = null;

			Log.d(Constants.TAG, "Device " + detachedDevice.getDeviceName() + " has been detached.");
			
			Message message = new Message();
			message.obj = detachedDevice;
			deviceDetachedHandler.sendMessage(message);
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useGL20 = false;
        
        initialize(new MidiShow(), cfg);
        
        /* MIDI Stuff */
        
		deviceDetachedHandler = new Handler(new Callback() {
			/*
			 * (non-Javadoc)
			 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
			 */
			@Override
			public boolean handleMessage(Message msg) {
				UsbDevice usbDevice = (UsbDevice) msg.obj;
				onDeviceDetached(usbDevice);
				return true;
			}
		});

		deviceConnectionWatcher = new MidiDeviceConnectionWatcher(getApplicationContext(), deviceAttachedListener, deviceDetachedListener);
    }

	@Override
	public void onMidiMiscellaneousFunctionCodes(MidiInputDevice sender,
			int cable, int byte1, int byte2, int byte3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiCableEvents(MidiInputDevice sender, int cable, int byte1,
			int byte2, int byte3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiSystemCommonMessage(MidiInputDevice sender, int cable,
			byte[] bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiSystemExclusive(MidiInputDevice sender, int cable,
			byte[] systemExclusive) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiNoteOff(MidiInputDevice sender, int cable, int channel,
			int note, int velocity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiNoteOn(MidiInputDevice sender, int cable, int channel,
			int note, int velocity) {
		// TODO Auto-generated method stub
		
		// Pass to listener
		((MidiShow) listener).onMidiNoteOn(channel, note, velocity);
	}

	@Override
	public void onMidiPolyphonicAftertouch(MidiInputDevice sender, int cable,
			int channel, int note, int pressure) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiControlChange(MidiInputDevice sender, int cable,
			int channel, int function, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiProgramChange(MidiInputDevice sender, int cable,
			int channel, int program) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiChannelAftertouch(MidiInputDevice sender, int cable,
			int channel, int pressure) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiPitchWheel(MidiInputDevice sender, int cable,
			int channel, int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMidiSingleByte(MidiInputDevice sender, int cable, int byte1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeviceAttached(final UsbDevice usbDevice) {
		Toast.makeText(this, "USB MIDI Device " + usbDevice.getDeviceName() + " has been attached.", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDeviceDetached(UsbDevice usbDevice) {
		// TODO Auto-generated method stub
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (deviceConnectionWatcher != null) {
			deviceConnectionWatcher.stop();
		}
		deviceConnectionWatcher = null;
		
		if (midiInputDevice != null) {
			midiInputDevice.stop();
			midiInputDevice = null;
		}
		
		midiOutputDevice = null;
	}
	
	/**
	 * Get MIDI output device, if available.
	 * 
	 * @param usbDevice
	 * @return MidiOutputDevice, null if not available
	 */
	public final MidiOutputDevice getMidiOutputDevice() {
		if (deviceConnectionWatcher != null) {
			deviceConnectionWatcher.checkConnectedDevicesImmediately();
		}
		
		return midiOutputDevice;
	}
}