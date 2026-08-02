import {
  Fan,
  Droplets,
  Shirt,
  Snowflake,
  Waves,
  Tv,
  Laptop,
  Lightbulb,
  Utensils,
  Coffee,
  Zap,
  BatteryCharging
} from 'lucide-react'

export const APPLIANCE_ICONS = {
  hvac: Fan,
  waterHeater: Droplets,
  dryer: Shirt,
  fridge: Snowflake,
  washer: Waves,
  tv: Tv,
  office: Laptop,
  lighting: Lightbulb,
  microwave: Utensils,
  coffee: Coffee,
  pool: Waves,
  ev: BatteryCharging
}

export function getApplianceIcon(category) {
  return APPLIANCE_ICONS[category] || Zap
}
