package com.meteor.ImmersiveMedia;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.snackbar.Snackbar;
import com.meteor.meteorproject.R;


public class CalibrateFragment extends Fragment implements DeviceAdapter.DeviceInteractionListener {

    private final boolean shouldReloadData = true;

    private ConstraintLayout newDeviceContent;
    private ConstraintLayout calibratedDeviceContent;
    private RecyclerView newDevicesRecyclerView;
    private RecyclerView calibratedDevicesRecyclerView;

    private List<Device> devicesAfterFiltered; // Renamed List
    private List<Device> calibratedDevices = new ArrayList<>();

    private DeviceAdapter deviceAdapter;
    private DeviceAdapter calibratedDeviceAdapter;

    private RecyclerView horizontalRecyclerView; // Add this for the horizontal RecyclerView


    //private final String newDevicesCsvFileName = "new_devices.csv";
    //private final String calibratedDevicesCsvFileName = "calibrated_devices.csv";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("CalibrateFragment", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_calibrate, container, false);


        // Step 1: Initialize Views
        initializeViews(view);
        initializeAdapters(); // Ensure this is called to set up adapters


        // Initialize Views
        newDeviceContent = view.findViewById(R.id.new_device_content_include);
        calibratedDeviceContent = view.findViewById(R.id.calibrated_device_content_include);

        // Initialize RecyclerViews
        horizontalRecyclerView = view.findViewById(R.id.horizontalRecyclerViewCalibrate);
        newDevicesRecyclerView = view.findViewById(R.id.deviceRecyclerViewCalibrate); // Replace with actual ID
        calibratedDevicesRecyclerView = view.findViewById(R.id.deviceRecyclerViewCalibrated);

        if (horizontalRecyclerView != null) {
            // Initialize the horizontal RecyclerView
            initializeHorizontalRecyclerView();
        } else {
            Log.e("CalibrateFragment", "horizontalRecyclerView is null");
        }

        if (calibratedDevicesRecyclerView != null) {
            // Initialize calibrated devices RecyclerView
            calibratedDeviceAdapter = new DeviceAdapter(getContext(), new ArrayList<>(), false, this);
            calibratedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            calibratedDevicesRecyclerView.setAdapter(calibratedDeviceAdapter);
        } else {
            Log.e("CalibrateFragment", "calibratedDevicesRecyclerView is null");
        }

        // Setup buttons and other initializations
        setupButtons(view);

        return view;
    }


    private void initializeHorizontalRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(layoutManager);

        List<Device> allDevices = loadDevicesFromRawResource();
        devicesAfterFiltered = filterDevicesWithWifiOn(allDevices);

        List<List<Device>> deviceColumns = divideDevicesIntoColumns(devicesAfterFiltered, 7); // Adjust the number as needed
        HorizontalColumnAdapter horizontalAdapter = new HorizontalColumnAdapter(deviceColumns);
        horizontalRecyclerView.setAdapter(horizontalAdapter);
    }

    // Horizontal RecyclerView Adapter
    private class HorizontalColumnAdapter extends RecyclerView.Adapter<HorizontalColumnAdapter.ColumnViewHolder> {
        private final List<List<Device>> deviceColumns;

        HorizontalColumnAdapter(List<List<Device>> deviceColumns) {
            this.deviceColumns = deviceColumns;
        }

        @NonNull
        @Override
        public ColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.column_for_new_device_content, parent, false);
            // Set the width of the view to one-third of the screen width
            int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;
            view.getLayoutParams().width = screenWidth / 3;

            return new ColumnViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ColumnViewHolder holder, int position) {
            holder.setDevices(deviceColumns.get(position));
        }

        @Override
        public int getItemCount() {
            return deviceColumns.size();
        }

        class ColumnViewHolder extends RecyclerView.ViewHolder {
            private final RecyclerView devicesRecyclerView;
            private final DeviceAdapter deviceAdapter;

            ColumnViewHolder(View itemView) {
                super(itemView);
                devicesRecyclerView = itemView.findViewById(R.id.deviceRecyclerViewCalibrate);
                devicesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

                // Pass CalibrateFragment as the listener to the adapter
                deviceAdapter = new DeviceAdapter(itemView.getContext(), new ArrayList<>(), true, CalibrateFragment.this);
                devicesRecyclerView.setAdapter(deviceAdapter);
            }

            void setDevices(List<Device> devices) {
                deviceAdapter.updateDevices(devices);
            }
        }
    }

    private List<List<Device>> divideDevicesIntoColumns(List<Device> devices, int devicesPerColumn) {
        List<List<Device>> columns = new ArrayList<>();
        int totalSize = devices.size();
        for (int i = 0; i < totalSize; i += devicesPerColumn) {
            int end = Math.min(i + devicesPerColumn, totalSize);
            columns.add(new ArrayList<>(devices.subList(i, end)));
        }
        return columns;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CalibrateFragment", "onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("CalibrateFragment", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("CalibrateFragment", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CalibrateFragment", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("CalibrateFragment", "onDestroy");
    }

    private void initializeViews(View view) {
        // Access the included layouts
        newDeviceContent = view.findViewById(R.id.new_device_content_include);
        calibratedDeviceContent = view.findViewById(R.id.calibrated_device_content_include);

        // If the newDeviceContent is not null, find the RecyclerView within this included layout
        if (newDeviceContent != null) {
            horizontalRecyclerView = newDeviceContent.findViewById(R.id.horizontalRecyclerViewCalibrate);
            newDevicesRecyclerView = newDeviceContent.findViewById(R.id.deviceRecyclerViewCalibrate); // Ensure this line is present
        } else {
            Log.e("CalibrateFragment", "newDeviceContent is null");
        }

        // Assuming calibratedDevicesRecyclerView is directly in the fragment's layout
        calibratedDevicesRecyclerView = view.findViewById(R.id.deviceRecyclerViewCalibrated);
        if (calibratedDevicesRecyclerView == null) {
            Log.e("CalibrateFragment", "calibratedDevicesRecyclerView is null");
        }
    }


    private void initializeAdapters() {
        List<Device> allDevices = loadDevicesFromRawResource();
        devicesAfterFiltered = filterDevicesWithWifiOn(allDevices);

        // Initialize the adapter for new devices
        if (newDevicesRecyclerView != null) {
            if (deviceAdapter == null) {
                deviceAdapter = new DeviceAdapter(getContext(), devicesAfterFiltered, true, this);
                Log.d("CalibrateFragment", "deviceAdapter initialized with " + devicesAfterFiltered.size() + " devices.");
                newDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                newDevicesRecyclerView.setAdapter(deviceAdapter);
            } else {
                deviceAdapter.updateDevices(devicesAfterFiltered);
            }
        } else {
            Log.e("CalibrateFragment", "newDevicesRecyclerView is null");
        }

        // Initialize the adapter for calibrated devices
        if (calibratedDevicesRecyclerView != null) {
            if (calibratedDeviceAdapter == null) {
                calibratedDeviceAdapter = new DeviceAdapter(getContext(), new ArrayList<>(), false, null);
                calibratedDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                calibratedDevicesRecyclerView.setAdapter(calibratedDeviceAdapter);
            } else {
                calibratedDeviceAdapter.updateDevices(calibratedDevices);
            }
        } else {
            Log.e("CalibrateFragment", "calibratedDevicesRecyclerView is null");
        }
    }



    private void setupButtons(View view) {
        // [Setup button click listeners]
        Button newDeviceButton = view.findViewById(R.id.NewDeviceTabButton);
        Button calibratedButton = view.findViewById(R.id.CalibratedTabButton);
        Button calibrateButton = view.findViewById(R.id.calibrateButton);

        newDeviceButton.setOnClickListener(v -> showNewDeviceContent());
        calibratedButton.setOnClickListener(v -> showCalibratedContent());
        calibrateButton.setOnClickListener(v -> {
            if (deviceAdapter == null) {
                Log.e("CalibrateFragment", "Device adapter is not initialized.");
                return;
            } else {
                // Handle the case where deviceAdapter is null, perhaps with a log statement or user notification
                Log.e("CalibrateFragment", "deviceAdapter is null when calibrate button is clicked.");
                Snackbar.make(view, "Error: Unable to calibrate devices at this time.", Snackbar.LENGTH_SHORT).show();
            }
        });

        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.toggleButton);
        toggleGroup.check(R.id.NewDeviceTabButton);
        showNewDeviceContent();
    }


    private void showNewDeviceContent() {
        // Show new device content logic
        newDeviceContent.setVisibility(View.VISIBLE);
        calibratedDeviceContent.setVisibility(View.GONE);
    }

    private void showCalibratedContent() {
        // Show calibrated device content logic
        calibratedDeviceContent.setVisibility(View.VISIBLE);
        newDeviceContent.setVisibility(View.GONE);
    }


    private List<Device> loadDevicesFromRawResource() {
        List<Device> devices = new ArrayList<>();

        try (InputStream is = getResources().openRawResource(R.raw.devices);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.d("CalibrateFragment", "Read line: " + line);
                String[] tokens = line.split(",");
                if (tokens.length == 4) { // Adjusted to expect 4 tokens per line
                    String name = tokens[0].trim();
                    int battery = Integer.parseInt(tokens[1].trim());
                    boolean isWifiOn = Boolean.parseBoolean(tokens[2].trim().toLowerCase());
                    boolean isCalibrated = Boolean.parseBoolean(tokens[3].trim().toLowerCase());

                    devices.add(new Device(name, battery, isWifiOn, isCalibrated));
                }
            }
        } catch (IOException e) {
            Log.e("CalibrateFragment", "Error reading from devices.csv", e);
        }
        Log.d("CalibrateFragment", "Loaded devices: " + devices.size());

        return devices;
    }

    private List<Device> filterDevicesWithWifiOn(List<Device> deviceList) {
        List<Device> filteredDevices = new ArrayList<>();
        for (Device device : deviceList) {
            if (device.isWifiOn()) {
                filteredDevices.add(device);
            }
        }
        return filteredDevices;
    }


    /*private void saveDevicesToCsv(List<Device> deviceList, String fileName) {
        // [Save devices to CSV file logic]
        File csvFile = new File(getContext().getFilesDir(), fileName);
        try (FileWriter writer = new FileWriter(csvFile)) {
            for (Device device : deviceList) {
                writer.write(device.toCsvString() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */


    // This method will be called when the calibrate button is clicked.
    private void calibrateSelectedDevices() {
        // Get selected devices from the new devices adapter
        List<Device> selectedDevices = deviceAdapter.getSelectedDevices();

        // Check for any selected devices
        if (selectedDevices.isEmpty()) {
            // You can show a message to the user that no devices are selected for calibration
            Snackbar.make(getView(), "No devices selected for calibration.", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // Move them to the calibrated list and remove from the current list
        for (Device device : selectedDevices) {
            device.setCalibrated(true); // Assuming there is a setCalibrated method in the Device class
            devicesAfterFiltered.remove(device);
            calibratedDevices.add(device);
        }

        // Here, you would also update the actual data source, e.g., a database, to reflect these changes

        // Notify the adapters about the data set changes
        deviceAdapter.notifyDataSetChanged();
        calibratedDeviceAdapter.updateDevices(calibratedDevices);

        // Deselect all devices in the new devices list after moving them to calibrated
        deviceAdapter.deselectAllDevices();
    }





    @Override
    public void onPause() {
        super.onPause();
        Log.d("CalibrateFragment", "onPause");

        //saveDevicesToCsv(devicesAfterFiltered, newDevicesCsvFileName);
        //saveDevicesToCsv(calibratedDevices, calibratedDevicesCsvFileName);
    }

    private List<Device> loadDevicesFromCsv(String fileName) {
        List<Device> devices = new ArrayList<>();
        File csvFile = new File(getContext().getFilesDir(), fileName);

        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
            } catch (IOException e) {
                Log.e("CalibrateFragment", "Error creating new CSV file: " + fileName, e);
            }
            return devices; // Return an empty list for a new file
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 4) {
                    String name = tokens[0].trim();
                    int battery = Integer.parseInt(tokens[1].trim());
                    boolean isWifiOn = Boolean.parseBoolean(tokens[2].trim().toLowerCase());
                    boolean isCalibrated = Boolean.parseBoolean(tokens[3].trim().toLowerCase());

                    devices.add(new Device(name, battery, isWifiOn, isCalibrated));
                }
            }
        } catch (IOException e) {
            Log.e("CalibrateFragment", "Error reading from " + fileName, e);
        }

        Log.d("CalibrateFragment", "Loaded " + devices.size() + " devices from " + fileName);
        return devices;
    }

    @Override
    public void onPingClicked(String deviceName) {
        // Show a Snackbar with the device name and "pinged!"
        View view = getView(); // Get the root view of the fragment
        if (view != null) {
            Snackbar.make(view, deviceName + " pinged!", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (shouldReloadData) {
            List<Device> allDevices = loadDevicesFromRawResource();
            devicesAfterFiltered = filterDevicesWithWifiOn(allDevices);

            if (deviceAdapter != null) {
                deviceAdapter.updateDevices(devicesAfterFiltered);
                Log.d("CalibrateFragment", "Devices reloaded and updated in deviceAdapter");
            } else {
                Log.e("CalibrateFragment", "deviceAdapter is null in onResume");
            }

            if (calibratedDeviceAdapter != null) {
                calibratedDeviceAdapter.updateDevices(calibratedDevices);
                Log.d("CalibrateFragment", "Devices reloaded and updated in calibratedDeviceAdapter");
            } else {
                Log.e("CalibrateFragment", "calibratedDeviceAdapter is null in onResume");
            }
        }
    }
}



