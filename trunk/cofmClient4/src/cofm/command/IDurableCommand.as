package cofm.command 
{

	public interface IDurableCommand extends ICommand {
		function redo(): void;
		function undo(): void;
		function setDurable(val: Boolean): void;
		function handleResponse(data: Object): void;
	}
}