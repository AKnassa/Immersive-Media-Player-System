package com.meteor.meteorproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AlldevicesFragment extends Fragment implements DeviceAdapter.DeviceInteractionListener {

    private RecyclerView parentRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alldevices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parentRecyclerView = view.findViewById(R.id.parentRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        parentRecyclerView.setLayoutManager(layoutManager);

        List<Device> devices = loadInitialDeviceListFromRaw();
        List<List<Device>> deviceColumns = divideDevicesIntoColumns(devices, 8); // Adjust the number 10 to how many items you want per column

        ParentAdapter parentAdapter = new ParentAdapter(deviceColumns);
        parentRecyclerView.setAdapter(parentAdapter);
    }

    private List<Device> loadInitialDeviceListFromRaw() {
        List<Device> devices = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.devices);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0].trim();
                    int battery = Integer.parseInt(parts[1].trim());
                    boolean wifiOn = Boolean.parseBoolean(parts[2].trim());
                    boolean isCalibrated = Boolean.parseBoolean(parts[3].trim());
                    devices.add(new Device(name, battery, wifiOn, isCalibrated));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return devices;
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

    // Parent RecyclerView Adapter
    private class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder> {
        private final List<List<Device>> deviceColumns;

        ParentAdapter(List<List<Device>> deviceColumns) {
            this.deviceColumns = deviceColumns;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.column_item, parent, false);

            // Set the width of the view to one-third of the screen width
            int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;
            view.getLayoutParams().width = screenWidth / 3;

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setDevices(deviceColumns.get(position));
        }

        @Override
        public int getItemCount() {
            return deviceColumns.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final RecyclerView devicesRecyclerView;
            private final DeviceAdapter deviceAdapter;

            ViewHolder(View itemView) {
                super(itemView);
                devicesRecyclerView = itemView.findViewById(R.id.deviceRecyclerView);
                devicesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

                // Pass the fragment as the listener to the adapter
                deviceAdapter = new DeviceAdapter(itemView.getContext(), new ArrayList<>(), false, AlldevicesFragment.this);
                devicesRecyclerView.setAdapter(deviceAdapter);
            }

            void setDevices(List<Device> devices) {
                deviceAdapter.updateDevices(devices);
            }
        }
    }

    // Implement the onPingClicked method from the DeviceInteractionListener interface
    @Override
    public void onPingClicked(String deviceName) {
        // Show a Snackbar with the device name and "pinged!"
        View view = getView(); // Get the root view of the fragment
        if (view != null) {
            Snackbar.make(view, deviceName + " pinged!", Snackbar.LENGTH_SHORT).show();
        }
    }
}
