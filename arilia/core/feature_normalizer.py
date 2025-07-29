"""Feature normalization utilities."""
from typing import Dict


def normalize_features(data: Dict[str, list]) -> Dict[str, int]:
    """Convert extracted behaviour data into structured features."""
    features = {f"ttp_{ttp}": 1 for ttp in data.get("ttps", [])}
    features.update({
        "uses_PowerShell": int("PowerShell" in data.get("tools", [])),
        "sector_telecom": int(data.get("sector") == "telecom"),
    })
    return features
