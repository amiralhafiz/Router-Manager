"""Ollama runner for behaviour extraction."""
from typing import Dict


def extract_behaviours(text: str) -> Dict[str, list]:
    """Use Ollama CLI or API to extract behaviours from raw text.

    Parameters
    ----------
    text: str
        Threat report text to analyze.

    Returns
    -------
    dict
        Dictionary containing extracted behaviours.
    """
    # TODO: Replace with actual Ollama integration
    return {
        "ttps": ["T1059", "T1075"],
        "tools": ["PowerShell"],
        "malware": ["PlugX"],
        "sector": "telecom",
    }
