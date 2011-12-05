package russell.john;

public class TextType
{
	private String date;	
	private String name;
	private String number;
	private String message;
	
	TextType(){}
	
	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getNumber()
	{
		return number;
	}
	public void setNumber(String number)
	{
		this.number = number;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	@Override
	public String toString()
	{
		return date + ", " + name + ", " + number + ", " + message;		
	}
	
}
