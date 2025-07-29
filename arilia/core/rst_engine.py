"""Mock Rough Set Theory engine for attribution."""
from typing import Dict


def classify_with_rough_set(features: Dict[str, int]) -> Dict[str, object]:
    """Classify using basic rough set-like logic (mocked)."""
    if features.get("ttp_T1059") and features.get("sector_telecom"):
        return {
            "group": "APT10",
            "confidence": 0.92,
            "explanation": "Detected PowerShell usage in telecom sector with PlugX.",
        }
    return {
        "group": "Unknown",
        "confidence": 0.5,
        "explanation": "Insufficient features for confident attribution.",
    }
