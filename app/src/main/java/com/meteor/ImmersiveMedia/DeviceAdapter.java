package com.meteor.ImmersiveMedia;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final List<Device> devices;
    private final LayoutInflater inflater;
    private final boolean isCalibrateFragment;
    private DeviceInteractionListener listener;

    public interface DeviceInteractionListener {
        void onPingClicked(String deviceName);
    }

    public DeviceAdapter(Context context, List<Device> devices, boolean isCalibrateFragment, DeviceInteractionListener listener) {
        this.devices = devices;
        this.inflater = LayoutInflater.from(context);
        this.isCalibrateFragment = isCalibrateFragment;
        this.listener = listener;
        Log.d("DeviceAdapter", "Adapter created with " + devices.size() + " devices.");
    }

    // Add a method in DeviceAdapter to get selected devices
    public List<Device> getSelectedDevices() {
        return devices.stream().filter(Device::isSelected).collect(Collectors.toList());
    }

    public void deselectAllDevices() {
        for (Device device : devices) {
            device.setSelected(false);
        }
        notifyDataSetChanged();
    }



    public void updateDevices(List<Device> newDevices) {
        devices.clear();
        if (newDevices != null && !newDevices.isEmpty()) {
            devices.addAll(newDevices);
        }
        notifyDataSetChanged();
        Log.d("DeviceAdapter", "Adapter updated. Current size: " + devices.size());
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(isCalibrateFragment ? R.layout.device_row_item : R.layout.list_item_device, parent, false);
        Log.d("DeviceAdapter", "onCreateViewHolder called.");
        return new DeviceViewHolder(itemView, isCalibrateFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        // Call the bind method to set the device data to the views
        holder.bind(device);

        // Avoid triggering listener when setting the checked state programmatically
        if (holder.checkBoxItems != null) {
            holder.checkBoxItems.setOnCheckedChangeListener(null);
            holder.checkBoxItems.setChecked(device.isSelected());
            holder.checkBoxItems.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Update device selected state when checkbox state changes
                device.setSelected(isChecked);
            });
        }

        holder.pingTextView.setOnClickListener(v -> listener.onPingClicked(device.getName()));
        Log.d("DeviceAdapter", "Binding device at position " + position + ": " + device.getName());
    }

    @Override
    public int getItemCount() {
        Log.d("DeviceAdapter", "getItemCount called. Item count: " + devices.size());
        return devices.size();
    }




    class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView batteryTextView;
        private final ImageView wifiStatusIcon;
        private final CheckBox checkBoxItems;
        private final TextView pingTextView;


        DeviceViewHolder(View itemView, boolean isCalibrateFragment) {
            super(itemView);
            // Initialize views for each layout
            if (isCalibrateFragment) {
                nameTextView = itemView.findViewById(R.id.device_name_calibrate);
                batteryTextView = itemView.findViewById(R.id.device_battery_calibrate);
                checkBoxItems = itemView.findViewById(R.id.device_checkbox);
                pingTextView = itemView.findViewById(R.id.ping);

                wifiStatusIcon = null; // Not used in calibrate layout
            } else {
                nameTextView = itemView.findViewById(R.id.device_name);
                batteryTextView = itemView.findViewById(R.id.device_battery);
                wifiStatusIcon = itemView.findViewById(R.id.device_status_icon);

                checkBoxItems = null; // Not used in all devices layout
                pingTextView = itemView.findViewById(R.id.ping);
            }
            setupListeners();
        }

        private void setupListeners() {
            // Listener for pingTextView (common in both layouts)
            if (pingTextView != null) {
                pingTextView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onPingClicked(devices.get(position).getName());
                    }
                });
            }

            // Listener for wifiStatusIcon (only in all devices layout)
            if (wifiStatusIcon != null) {
                wifiStatusIcon.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Device device = devices.get(position);
                        device.setWifiOn(!device.isWifiOn());
                        updateWifiIcon(device.isWifiOn());
                    }
                });
            }

            if (checkBoxItems != null) {
                checkBoxItems.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Device device = devices.get(position);
                        device.setSelected(isChecked);

                        // Notify the fragment that an individual item has been clicked, if necessary.
                        // Do not call updateAllDeviceSelections here, because that's for the master checkbox logic.
                    }
                });
            }

        }

        private void updateWifiIcon(boolean isWifiOn) {
            if (wifiStatusIcon != null) {
                wifiStatusIcon.setImageResource(isWifiOn ? R.drawable.on_wifi : R.drawable.off_wifi);
            }
        }



        void bind(final Device device) {
            // Binding for name and battery (common in both layouts)
            nameTextView.setText(device.getName());
            batteryTextView.setText(String.format("%d%%", device.getBatteryPercentage()));

            // Binding for checkBox (only in calibrate layout)
            if (checkBoxItems != null) {
                checkBoxItems.setChecked(device.isSelected());
            }

            // Binding for wifiStatusIcon (only in all devices layout)
            if (wifiStatusIcon != null) {
                updateWifiIcon(device.isWifiOn());
            }

            Log.d("DeviceViewHolder", "Device bound: " + device.getName());
        }
    }
}

