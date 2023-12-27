
from abc import ABC, abstractmethod 


class AbstractGameModel(ABC):
    """An abstract class which will represent all games playable within the system"""

    @abstractmethod
    def get_header_info(self) -> dict:
        """Get all overview information related to an implementation of game state.
        
        Primarily used for """
        pass